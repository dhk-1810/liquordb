import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function Home() {
  const [trendingLiquors, setTrendingLiquors] = useState({
    THREE_HOURS: [],
    DAILY: [],
    WEEKLY: []
  });
  const [activeTab, setActiveTab] = useState('THREE_HOURS');
  const [pinnedNotices, setPinnedNotices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [threeHoursRes, dailyRes, weeklyRes, noticesRes] = await Promise.all([
          fetch('/api/liquors/trending?period=THREE_HOURS'),
          fetch('/api/liquors/trending?period=DAILY'),
          fetch('/api/liquors/trending?period=WEEKLY'),
          fetch('/api/notices')
        ]);
        
        const threeHoursData = await threeHoursRes.json();
        const dailyData = await dailyRes.json();
        const weeklyData = await weeklyRes.json();
        const noticesData = await noticesRes.json();
        
        setTrendingLiquors({
          THREE_HOURS: Array.isArray(threeHoursData) ? threeHoursData : [],
          DAILY: Array.isArray(dailyData) ? dailyData : [],
          WEEKLY: Array.isArray(weeklyData) ? weeklyData : []
        });
        
        const allNotices = noticesData.content || [];
        setPinnedNotices(allNotices.filter((n) => n.isPinned));
        
      } catch (err) {
        console.error('Failed to fetch data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const currentTrending = trendingLiquors[activeTab] || [];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-12 mb-20 animate-fade-in-up">
      {loading ? (
        <div className="flex flex-col justify-center items-center h-64 gap-4">
          <div className="relative w-16 h-16">
            <div className="absolute inset-0 rounded-full border-4 border-slate-100"></div>
            <div className="absolute inset-0 rounded-full border-4 border-amber-500 border-t-transparent animate-spin"></div>
          </div>
          <p className="text-slate-500 font-medium">Loading amazing drinks...</p>
        </div>
      ) : (
        <div className="space-y-20">
          {/* Pinned Notices Section */}
          {pinnedNotices.length > 0 && (
            <section className="animate-fade-in-up">
              <div className="flex items-center gap-3 mb-6">
                <div className="p-2.5 bg-amber-100 rounded-xl text-amber-600 shadow-sm border border-amber-200/50">
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
                  </svg>
                </div>
                <div>
                  <h2 className="text-2xl font-bold text-slate-900 tracking-tight">Important Notices</h2>
                  <p className="text-slate-500 text-sm mt-0.5">Stay updated with the latest news</p>
                </div>
              </div>
              
              <div className="grid gap-3">
                {pinnedNotices.map((notice, idx) => (
                  <div key={idx} className="group flex flex-col sm:flex-row sm:items-center justify-between bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:shadow-lg hover:border-amber-300 transition-all duration-300 cursor-pointer relative overflow-hidden">
                    <div className="absolute left-0 top-0 bottom-0 w-1 bg-amber-400 transform origin-left scale-y-0 group-hover:scale-y-100 transition-transform duration-300 ease-out z-10"></div>
                    <div className="flex items-start sm:items-center gap-4 relative z-20">
                      <span className="flex-shrink-0 w-8 h-8 rounded-full bg-amber-50 flex items-center justify-center text-amber-500 group-hover:bg-amber-500 group-hover:text-white transition-colors duration-300 mt-1 sm:mt-0">
                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                      </span>
                      <h3 className="text-lg font-semibold text-slate-800 group-hover:text-amber-700 transition-colors">
                        {notice.title}
                      </h3>
                    </div>
                    <span className="text-sm font-medium text-slate-400 mt-3 sm:mt-0 bg-slate-50 px-3 py-1 rounded-full self-start sm:self-auto border border-slate-100">
                      {new Date(notice.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })}
                    </span>
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Trending Liquors Section */}
          <section className="animate-fade-in-up" style={{ animationDelay: '150ms' }}>
            <div className="flex flex-col sm:flex-row sm:items-end justify-between mb-8 gap-4">
              <div className="flex flex-col gap-5">
                <div className="flex items-center gap-3">
                  <div className="p-2.5 bg-rose-100 rounded-xl text-rose-600 shadow-sm border border-rose-200/50">
                    <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                    </svg>
                  </div>
                  <div>
                    <h2 className="text-3xl font-bold text-slate-900 tracking-tight">Trending Now</h2>
                    <p className="text-slate-500 text-sm mt-1 flex items-center gap-1.5">
                      <span className="w-2 h-2 rounded-full bg-rose-500 animate-pulse"></span>
                      Most loved drinks according to your community
                    </p>
                  </div>
                </div>
                
                {/* Scope Tabs */}
                <div className="flex items-center gap-1 bg-slate-200/50 p-1 rounded-xl self-start">
                  {[
                    { id: 'THREE_HOURS', label: 'Real-time' },
                    { id: 'DAILY', label: 'Daily Top 10' },
                    { id: 'WEEKLY', label: 'Weekly Top 10' }
                  ].map((tab) => (
                    <button
                      key={tab.id}
                      onClick={() => setActiveTab(tab.id)}
                      className={`px-4 py-2 rounded-lg text-sm font-semibold transition-all duration-300 ${
                        activeTab === tab.id 
                          ? 'bg-white text-rose-600 shadow-sm border-slate-200/50' 
                          : 'text-slate-500 hover:text-slate-700 hover:bg-slate-300/50'
                      }`}
                    >
                      {tab.label}
                    </button>
                  ))}
                </div>
              </div>
              
              <button className="text-sm font-semibold text-amber-600 hover:text-amber-700 bg-amber-50 hover:bg-amber-100 px-5 py-2 rounded-full transition-colors flex items-center gap-1.5 shadow-sm border border-amber-200/50 hidden sm:flex">
                View all
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>

            {currentTrending.length === 0 ? (
              <div className="text-center py-24 bg-white/50 backdrop-blur-sm rounded-3xl border border-slate-200/60 shadow-sm">
                <div className="w-16 h-16 mx-auto bg-slate-100 rounded-full flex items-center justify-center text-slate-400 mb-4">
                  <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                  </svg>
                </div>
                <h3 className="text-xl font-bold text-slate-600">No trending records found</h3>
                <p className="text-slate-400 mt-2">Check back later for popular picks.</p>
              </div>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
                {currentTrending.slice(0, 10).map((liquor, index) => (
                  <div 
                    key={liquor.id} 
                    className="group bg-white rounded-3xl border border-slate-200 overflow-hidden flex flex-col cursor-pointer relative transition-all duration-300 hover:shadow-xl hover:shadow-amber-900/5 hover:-translate-y-1 hover:border-amber-200"
                  >
                    {/* Ranking Badge */}
                    <div className="absolute top-4 left-4 z-10 w-10 h-10 bg-gradient-to-br from-slate-800 to-slate-900 text-amber-400 rounded-full flex items-center justify-center font-black text-lg shadow-lg border border-slate-700 backdrop-blur-md transform group-hover:scale-110 transition-transform">
                      {index + 1}
                    </div>

                    {/* Image Container */}
                    <div className="relative aspect-[4/3] w-full bg-slate-100 overflow-hidden">
                      <img
                        src={liquor.imageUrl || 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&q=80'}
                        alt={liquor.name}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out"
                        onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&q=80' }}
                      />
                      <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                      
                      <div className="absolute top-4 right-4 bg-white/95 backdrop-blur-md px-3 py-1.5 rounded-xl shadow-lg flex items-center gap-1.5 border border-slate-100 transform group-hover:-translate-y-1 transition-transform">
                        <svg className="w-4 h-4 text-amber-500" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                        </svg>
                        <span className="text-sm font-bold text-slate-800 tracking-tight">{liquor.averageRating?.toFixed(1) || '0.0'}</span>
                      </div>

                      <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 z-10 pointer-events-none">
                         <span className="bg-white/20 backdrop-blur-md text-white font-semibold px-6 py-2.5 rounded-full border border-white/30 shadow-2xl backdrop-saturate-150">
                           View Details
                         </span>
                      </div>
                    </div>

                    {/* Content */}
                    <div className="p-6 flex flex-col flex-grow relative bg-white z-20">
                      <div className="mb-4">
                        <h3 className="text-xl font-bold text-slate-900 group-hover:text-amber-600 transition-colors line-clamp-1 mb-1">
                          {liquor.name}
                        </h3>
                      </div>
                      
                      <div className="mt-auto pt-5 border-t border-slate-100 flex items-center justify-between text-slate-500 text-sm font-medium">
                        <div className="flex items-center gap-5">
                          <div className="flex items-center gap-2 group/icon transition-transform hover:scale-110">
                            <svg className="w-4.5 h-4.5 text-slate-300 group-hover/icon:text-amber-500 transition-colors" fill="currentColor" viewBox="0 0 20 20">
                              <path d="M2 10.5a1.5 1.5 0 113 0v6a1.5 1.5 0 01-3 0v-6zM6 10.333v5.43a2 2 0 001.106 1.79l.05.025A4 4 0 008.943 18h5.416a2 2 0 001.962-1.608l1.2-6A2 2 0 0015.56 8H12V4a2 2 0 00-2-2 1 1 0 00-1 1v.667a4 4 0 01-.8 2.4L6.8 7.933a4 4 0 00-.8 2.4z" />
                            </svg>
                            <span className="group-hover/icon:text-slate-800 transition-colors">{liquor.reviewCount || 0}</span>
                          </div>
                          <div className="flex items-center gap-2 group/icon transition-transform hover:scale-110">
                            <svg className={`w-4.5 h-4.5 transition-colors ${liquor.likedByMe ? 'fill-red-500 text-red-500' : 'text-slate-300 group-hover/icon:text-red-500'}`} fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" />
                            </svg>
                            <span className="group-hover/icon:text-slate-800 transition-colors">{liquor.likeCount || 0}</span>
                          </div>
                        </div>
                        
                        <div className="w-10 h-10 rounded-full bg-slate-50 flex items-center justify-center group-hover:bg-amber-50 group-hover:text-amber-500 transition-all text-slate-300 shadow-sm group-hover:shadow-md border border-transparent group-hover:border-amber-100">
                          <svg className="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                          </svg>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  );
}

export default Home;
