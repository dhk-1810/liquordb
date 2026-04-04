import { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import Home from './pages/Home';
import SignUp from './pages/SignUp';
import SignIn from './pages/SignIn';

function AppContent() {
  const [user, setUser] = useState(null);
  const [profileImageUrl, setProfileImageUrl] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    const checkAuth = async () => {
      // Avoid hitting token-refresh for anonymous visitors
      if (localStorage.getItem('isLoggedIn') !== 'true') {
        setAuthLoading(false);
        return;
      }

      try {
        const refreshRes = await fetch('/api/auth/token-refresh', { method: 'POST' });
        if (refreshRes.ok) {
          const jwtData = await refreshRes.json();
          setUser(jwtData.userDto);
          
          if (jwtData.userDto && jwtData.accessToken) {
            const myPageRes = await fetch(`/api/users/${jwtData.userDto.id}/my-page`, {
              headers: {
                'Authorization': `Bearer ${jwtData.accessToken}`
              }
            });
            if (myPageRes.ok) {
              const myPageData = await myPageRes.json();
              setProfileImageUrl(myPageData.imageUrl);
            }
          }
        } else {
          // If token refresh fails, clear the logged in flag
          localStorage.removeItem('isLoggedIn');
          setUser(null);
        }
      } catch (err) {
        console.error('Auth check failed:', err);
      } finally {
        setAuthLoading(false);
      }
    };

    checkAuth();
  }, [location.pathname]); // re-check on auth pages if needed, or just on mount

  return (
    <div className="min-h-screen bg-slate-50 font-sans text-slate-900 pb-12 overflow-x-hidden flex flex-col">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-md border-b border-slate-200 sticky top-0 z-50 shadow-sm transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <Link to="/" className="text-2xl font-bold tracking-tight text-slate-900 flex items-center gap-2 hover:scale-105 transition-transform cursor-pointer">
              <span className="text-amber-500 text-3xl drop-shadow-sm">🥃</span> LiquorDB
            </Link>
            
            <div className="flex items-center text-sm font-medium text-slate-600">
              <div className="hidden sm:flex items-center gap-6">
                <Link to="/" className="text-amber-600 hover:text-amber-700 transition-colors">Home</Link>
                <span className="hover:text-amber-600 cursor-pointer transition-colors">Categories</span>
                <span className="hover:text-amber-600 cursor-pointer transition-colors">About</span>
              </div>
              <div className="flex items-center gap-4 ml-4 pl-4 sm:ml-6 sm:pl-6 border-l border-slate-200">
                {authLoading ? (
                  <div className="w-10 h-10 rounded-full bg-slate-200 animate-pulse"></div>
                ) : user ? (
                  <Link to="/mypage" className="focus:outline-none flex items-center justify-center transform hover:scale-105 transition-transform" title="Go to My Page">
                    {profileImageUrl ? (
                      <img src={profileImageUrl} alt={user.username} className="w-10 h-10 rounded-full object-cover border-2 border-white shadow-sm hover:border-amber-400 transition-colors bg-white" />
                    ) : (
                      <div className="w-10 h-10 rounded-full bg-amber-100 text-amber-700 flex items-center justify-center font-bold text-lg border-2 border-white shadow-sm hover:border-amber-400 transition-colors uppercase">
                        {user.username ? user.username.charAt(0) : '?'}
                      </div>
                    )}
                  </Link>
                ) : (
                  <>
                    <Link to="/signin" className="hover:text-amber-600 transition-colors font-semibold">Sign In</Link>
                    <Link to="/signup" className="bg-amber-500 hover:bg-amber-600 text-white px-5 py-2.5 rounded-full font-semibold transition-all shadow-sm shadow-amber-500/20 ml-1 hover:-translate-y-0.5">Sign Up</Link>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-grow">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/signin" element={<SignIn />} />
          <Route path="/mypage" element={
            <div className="text-center py-32 animate-fade-in-up">
              <h1 className="text-3xl font-bold text-slate-800">My Page (Coming Soon)</h1>
            </div>
          } />
        </Routes>
      </main>

      <style>{`
        @keyframes fadeInUp {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
        .animate-fade-in-up {
          animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
          opacity: 0;
        }
      `}</style>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}

export default App;
