import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchAuthToken } from '../utils/auth';

function MyPage() {
  const navigate = useNavigate();
  
  const [activeTab, setActiveTab] = useState('overview');
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [myPageData, setMyPageData] = useState(null);

  // Profile Update State
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [profileImage, setProfileImage] = useState(null);
  const [profileImagePreview, setProfileImagePreview] = useState(null);
  const [deleteProfileImage, setDeleteProfileImage] = useState(false);
  const [isUpdatingProfile, setIsUpdatingProfile] = useState(false);

  // Password Update State
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isUpdatingPassword, setIsUpdatingPassword] = useState(false);

  const fetchMyPageData = async () => {
    setIsLoading(true);
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert('Please log in first.');
        navigate('/signin');
        return;
      }
      setUser(jwtData.userDto);
      
      const response = await fetch(`/api/users/${jwtData.userDto.id}/my-page`, {
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });

      if (!response.ok) throw new Error('Failed to load my page data');
      const data = await response.json();
      setMyPageData(data);
      setUsername(data.username);
      setEmail(data.email);
    } catch (err) {
      console.error(err);
      window.alert('Error loading profile information.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchMyPageData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setProfileImage(file);
      setProfileImagePreview(URL.createObjectURL(file));
      setDeleteProfileImage(false);
    }
  };

  const handleProfileUpdate = async (e) => {
    e.preventDefault();
    setIsUpdatingProfile(true);

    try {
      const jwtData = await fetchAuthToken();
      
      const formData = new FormData();
      formData.append('username', username);
      // formData.append('email', email); // Assuming we don't update email easily, or maybe we do? Let's leave it up to username and image for now since email changes are sensitive.
      
      if (deleteProfileImage) {
        formData.append('deleteProfileImage', 'true');
      }
      
      if (profileImage) {
        formData.append('profileImage', profileImage);
      }

      const response = await fetch(`/api/users/${user.id}/update`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        },
        body: formData
      });

      if (!response.ok) {
        const err = await response.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to update profile');
      }

      window.alert('Profile updated successfully!');
      
      // If the username changed, the backend set a new refresh cookie and returned a new access token.
      // But since fetchAuthToken caches the old promise, we can force a page reload to cleanly restart auth state, 
      // or we can just fetch my page data again.
      // Easiest robust way is to just reload the page so App.jsx picks up the new token/username.
      window.location.reload();
      
    } catch (err) {
      console.error(err);
      window.alert(`Error: ${err.message}`);
    } finally {
      setIsUpdatingProfile(false);
    }
  };

  const handlePasswordUpdate = async (e) => {
    e.preventDefault();
    
    if (newPassword !== confirmPassword) {
      window.alert("New passwords don't match.");
      return;
    }

    setIsUpdatingPassword(true);
    try {
      const jwtData = await fetchAuthToken();
      
      const response = await fetch(`/api/users/${user.id}/update-password`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtData.accessToken}`
        },
        body: JSON.stringify({
          currentPassword,
          newPassword
        })
      });

      if (!response.ok) {
        const err = await response.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to update password');
      }

      window.alert('Password updated successfully!');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      console.error(err);
      window.alert(`Error: ${err.message}`);
    } finally {
      setIsUpdatingPassword(false);
    }
  };

  const handleWithdraw = async () => {
    if (window.confirm("Are you sure you want to delete your account? This action cannot be undone.")) {
      try {
        const jwtData = await fetchAuthToken();
        const response = await fetch(`/api/users/${user.id}`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${jwtData.accessToken}`
          }
        });
        if (!response.ok) throw new Error("Failed to delete account");
        
        window.alert("Account deleted successfully.");
        await fetch('/api/auth/logout', { method: 'POST' });
        localStorage.removeItem('isLoggedIn');
        window.location.href = '/';
      } catch (err) {
        console.error(err);
        window.alert("Error deleting account.");
      }
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-5xl mx-auto px-4 py-20 flex justify-center items-center">
        <svg className="animate-spin h-12 w-12 text-amber-500" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
        </svg>
      </div>
    );
  }

  if (!myPageData) return null;

  const displayImageUrl = profileImagePreview || (!deleteProfileImage ? myPageData.imageUrl : null);

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      
      {/* Header Profile Summary */}
      <div className="bg-white rounded-3xl p-8 shadow-sm border border-slate-200 mb-8 flex items-center gap-6">
        <div className="w-24 h-24 rounded-full bg-amber-100 flex items-center justify-center border-4 border-slate-50 shadow-md overflow-hidden flex-shrink-0">
          {myPageData.imageUrl ? (
            <img src={myPageData.imageUrl} alt={myPageData.username} className="w-full h-full object-cover" />
          ) : (
            <span className="text-3xl font-bold text-amber-700 uppercase">{myPageData.username.charAt(0)}</span>
          )}
        </div>
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900">{myPageData.username}</h1>
          <p className="text-slate-500 font-medium mt-1">{myPageData.email}</p>
        </div>
      </div>

      <div className="flex flex-col md:flex-row gap-8">
        
        {/* Sidebar */}
        <div className="w-full md:w-64 flex-shrink-0">
          <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-3 flex flex-col gap-1 sticky top-28">
            <button 
              onClick={() => setActiveTab('overview')}
              className={`w-full text-left px-4 py-3 rounded-xl font-semibold transition-colors ${activeTab === 'overview' ? 'bg-amber-50 text-amber-700' : 'text-slate-600 hover:bg-slate-50'}`}
            >
              Overview
            </button>
            <button 
              onClick={() => setActiveTab('edit_profile')}
              className={`w-full text-left px-4 py-3 rounded-xl font-semibold transition-colors ${activeTab === 'edit_profile' ? 'bg-amber-50 text-amber-700' : 'text-slate-600 hover:bg-slate-50'}`}
            >
              Edit Profile
            </button>
            <button 
              onClick={() => setActiveTab('change_password')}
              className={`w-full text-left px-4 py-3 rounded-xl font-semibold transition-colors ${activeTab === 'change_password' ? 'bg-amber-50 text-amber-700' : 'text-slate-600 hover:bg-slate-50'}`}
            >
              Change Password
            </button>
          </div>
        </div>

        {/* Content Area */}
        <div className="flex-grow">
          {activeTab === 'overview' && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 animate-fade-in-up">
              
              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-xl flex items-center justify-center mb-4">
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
                </div>
                <p className="text-slate-500 font-semibold mb-1">Liked Liquors</p>
                <p className="text-3xl font-extrabold text-slate-800">{myPageData.likedLiquorCount}</p>
              </div>

              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                <div className="w-12 h-12 bg-purple-100 text-purple-600 rounded-xl flex items-center justify-center mb-4">
                  <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 20 20"><path d="M2 10.5a1.5 1.5 0 113 0v6a1.5 1.5 0 01-3 0v-6zM6 10.333v5.43a2 2 0 001.106 1.79l.05.025A4 4 0 008.943 18h5.416a2 2 0 001.962-1.608l1.2-6A2 2 0 0015.56 8H12V4a2 2 0 00-2-2 1 1 0 00-1 1v.667a4 4 0 01-.8 2.4L6.8 7.933a4 4 0 00-.8 2.4z" /></svg>
                </div>
                <p className="text-slate-500 font-semibold mb-1">Liked Reviews</p>
                <p className="text-3xl font-extrabold text-slate-800">{myPageData.likedReviewCount}</p>
              </div>

              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                <div className="w-12 h-12 bg-indigo-100 text-indigo-600 rounded-xl flex items-center justify-center mb-4">
                  <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 20 20"><path d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" /></svg>
                </div>
                <p className="text-slate-500 font-semibold mb-1">Liked Comments</p>
                <p className="text-3xl font-extrabold text-slate-800">{myPageData.likedCommentCount}</p>
              </div>

              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                <div className="w-12 h-12 bg-amber-100 text-amber-600 rounded-xl flex items-center justify-center mb-4">
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                </div>
                <p className="text-slate-500 font-semibold mb-1">Reviews Written</p>
                <p className="text-3xl font-extrabold text-slate-800">{myPageData.reviewCount}</p>
              </div>

              <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                <div className="w-12 h-12 bg-green-100 text-green-600 rounded-xl flex items-center justify-center mb-4">
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" /></svg>
                </div>
                <p className="text-slate-500 font-semibold mb-1">Comments Written</p>
                <p className="text-3xl font-extrabold text-slate-800">{myPageData.commentCount}</p>
              </div>

            </div>
          )}

          {activeTab === 'edit_profile' && (
            <div className="animate-fade-in-up">
              {/* Edit Profile */}
              <div className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
                <div className="px-8 py-6 border-b border-slate-200 bg-slate-50">
                  <h2 className="text-xl font-bold text-slate-800">Edit Profile</h2>
                </div>
                <form onSubmit={handleProfileUpdate} className="p-8">
                  
                  <div className="flex flex-col sm:flex-row gap-8 mb-8">
                    <div className="flex flex-col items-center gap-4">
                      <div className="w-32 h-32 rounded-full border-4 border-slate-100 bg-slate-200 flex items-center justify-center overflow-hidden">
                        {displayImageUrl ? (
                          <img src={displayImageUrl} alt="Preview" className="w-full h-full object-cover" />
                        ) : (
                          <svg className="w-12 h-12 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
                        )}
                      </div>
                      
                      <div className="flex flex-col items-center gap-2">
                        <label className="bg-amber-100 hover:bg-amber-200 text-amber-700 text-sm font-bold py-2 px-4 rounded-lg cursor-pointer transition-colors text-center w-full">
                          Change Photo
                          <input type="file" accept="image/*" onChange={handleImageChange} className="hidden" />
                        </label>
                        {displayImageUrl && (
                          <button 
                            type="button" 
                            onClick={() => {
                              setDeleteProfileImage(true);
                              setProfileImage(null);
                              setProfileImagePreview(null);
                            }}
                            className="text-red-500 text-sm font-semibold hover:underline"
                          >
                            Remove
                          </button>
                        )}
                      </div>
                    </div>

                    <div className="flex-grow space-y-5">
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Username</label>
                        <input 
                          type="text" 
                          value={username}
                          onChange={(e) => setUsername(e.target.value)}
                          className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-semibold text-slate-700 mb-2">Email Address (Read Only)</label>
                        <input 
                          type="email" 
                          value={email}
                          readOnly
                          className="w-full bg-slate-100 border border-slate-200 rounded-xl px-4 py-3 text-slate-500 cursor-not-allowed"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-6 border-t border-slate-100">
                    <button 
                      type="button"
                      onClick={handleWithdraw}
                      className="text-sm font-semibold text-slate-400 hover:text-red-500 transition-colors"
                    >
                      Delete Account
                    </button>
                    <button 
                      type="submit" 
                      disabled={isUpdatingProfile}
                      className="bg-amber-500 hover:bg-amber-600 text-white font-bold py-3 px-8 rounded-xl transition-all shadow-md disabled:opacity-50"
                    >
                      {isUpdatingProfile ? 'Saving...' : 'Save Profile'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {activeTab === 'change_password' && (
            <div className="animate-fade-in-up">
              {/* Change Password */}
              <div className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
                <div className="px-8 py-6 border-b border-slate-200 bg-slate-50">
                  <h2 className="text-xl font-bold text-slate-800">Change Password</h2>
                </div>
                <form onSubmit={handlePasswordUpdate} className="p-8 space-y-5">
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">Current Password</label>
                    <input 
                      type="password" 
                      required
                      value={currentPassword}
                      onChange={(e) => setCurrentPassword(e.target.value)}
                      className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">New Password</label>
                    <input 
                      type="password" 
                      required
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">Confirm New Password</label>
                    <input 
                      type="password" 
                      required
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
                    />
                  </div>
                  
                  <div className="flex justify-end pt-6 mt-6 border-t border-slate-100">
                    <button 
                      type="submit" 
                      disabled={isUpdatingPassword}
                      className="bg-slate-800 hover:bg-slate-900 text-white font-bold py-3 px-8 rounded-xl transition-all shadow-md disabled:opacity-50"
                    >
                      {isUpdatingPassword ? 'Updating...' : 'Update Password'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
        </div>
      </div>

    </div>
  );
}

export default MyPage;
