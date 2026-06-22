import { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import Home from './pages/Home';
import SignUp from './pages/SignUp';
import SignIn from './pages/SignIn';
import LiquorsExplore from './pages/LiquorsExplore';
import NoticesList from './pages/NoticesList';
import NoticeDetail from './pages/NoticeDetail';
import LiquorDetail from './pages/LiquorDetail';
import ReviewWrite from './pages/ReviewWrite';
import MyPage from './pages/MyPage';
import MyActivityList from './pages/MyActivityList';
import NotificationDropdown from './components/NotificationDropdown';
import { fetchAuthToken } from './utils/auth';

function AppContent() {
  const [user, setUser] = useState(null);
  const [profileImageUrl, setProfileImageUrl] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);
  const location = useLocation();

  const isHomeActive = location.pathname === '/';
  const isLiquorsActive = location.pathname.startsWith('/liquors');
  const isNoticesActive = location.pathname.startsWith('/notices');
  const isMyPageActive = location.pathname.startsWith('/mypage');

  useEffect(() => {
    const checkAuth = async () => {
      // Avoid hitting token-refresh for anonymous visitors
      if (localStorage.getItem('isLoggedIn') !== 'true') {
        setAuthLoading(false);
        return;
      }

      try {
        const jwtData = await fetchAuthToken();
        if (jwtData) {
          setUser(jwtData.userDto);
          
          if (jwtData.userDto && jwtData.accessToken) {
            const myPageRes = await fetch(`/api/users/${jwtData.userDto.id}/my-page`, {
              headers: {
                'Authorization': `Bearer ${jwtData.accessToken}`
              }
            });
            if (myPageRes.ok) {
              const myPageData = await myPageRes.json();
              const url = myPageData.imageUrl;
              setProfileImageUrl(url && !url.includes('default-profile-image.png') ? url : '/default-avatar.svg');
            }
          }
        } else {
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

  const handleSignOut = async () => {
    try {
      await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) {
      console.error('Logout failed:', e);
    } finally {
      localStorage.removeItem('isLoggedIn');
      setUser(null);
      window.location.href = '/';
    }
  };

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
                <Link to="/" className={`transition-colors font-semibold ${isHomeActive ? 'text-amber-600 font-bold' : 'text-slate-600 hover:text-amber-600'}`}>Home</Link>
                <Link to="/liquors" className={`transition-colors font-semibold ${isLiquorsActive ? 'text-amber-600 font-bold' : 'text-slate-600 hover:text-amber-600'}`}>Liquors</Link>
                <Link to="/notices" className={`transition-colors font-semibold ${isNoticesActive ? 'text-amber-600 font-bold' : 'text-slate-600 hover:text-amber-600'}`}>Notices</Link>
                <span className="hover:text-amber-600 cursor-pointer transition-colors">About</span>
              </div>
              <div className="flex items-center gap-4 ml-4 pl-4 sm:ml-6 sm:pl-6 border-l border-slate-200">
                {authLoading ? (
                  <div className="w-10 h-10 rounded-full bg-slate-200 animate-pulse"></div>
                ) : user ? (
                  <>
                    <Link to="/mypage" className="focus:outline-none flex items-center justify-center transform hover:scale-105 transition-transform" title="Go to My Page">
                      <img src={profileImageUrl || '/default-avatar.svg'} alt={user.username} className={`w-10 h-10 rounded-full object-cover border-2 shadow-sm transition-colors bg-white ${isMyPageActive ? 'border-amber-500' : 'border-white hover:border-amber-400'}`} />
                    </Link>
                    <NotificationDropdown />
                    <button onClick={handleSignOut} className="hover:text-amber-600 transition-colors font-semibold ml-2">
                      Sign Out
                    </button>
                  </>
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
          <Route path="/liquors" element={<LiquorsExplore />} />
          <Route path="/liquors/:id" element={<LiquorDetail />} />
          <Route path="/liquors/:id/reviews/new" element={<ReviewWrite />} />
          <Route path="/notices" element={<NoticesList />} />
          <Route path="/notices/:id" element={<NoticeDetail />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/mypage/:category" element={<MyActivityList />} />
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
