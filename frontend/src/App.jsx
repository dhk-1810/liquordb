import { useEffect, useState } from 'react';

function App() {
  const [liquors, setLiquors] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('/api/liquors')
      .then((res) => res.json())
      .then((data) => {
        setLiquors(data.content || []);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Failed to fetch liquors:', err);
        setLoading(false);
      });
  }, []);

  return (
    <div className="min-h-screen bg-slate-50 font-sans text-slate-900 pb-12">
      {/* Header */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-10 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-5">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold tracking-tight text-slate-900 flex items-center gap-2">
              <span className="text-amber-500 text-3xl">🥃</span> LiquorDB
            </h1>
            <div className="text-sm font-medium text-slate-500 bg-slate-100 px-3 py-1 rounded-full border border-slate-200">
              {liquors.length} items
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-10">
        {loading ? (
          <div className="flex justify-center items-center h-64">
             <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-amber-500"></div>
          </div>
        ) : liquors.length === 0 ? (
          <div className="text-center py-20">
            <h3 className="text-xl font-medium text-slate-500">No liquors found.</h3>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            {liquors.map((liquor) => (
              <div 
                key={liquor.id} 
                className="group bg-white rounded-3xl border border-slate-100 shadow-sm hover:shadow-xl hover:border-amber-200 transition-all duration-300 overflow-hidden flex flex-col cursor-pointer"
              >
                {/* Image Container */}
                <div className="relative aspect-[4/3] w-full bg-slate-100 overflow-hidden">
                  <img
                    src={liquor.imageUrl || 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&q=80'}
                    alt={liquor.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 ease-out"
                    onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&q=80' }}
                  />
                  <div className="absolute top-3 right-3 bg-white/95 backdrop-blur-sm px-2.5 py-1.5 rounded-xl shadow-sm flex items-center gap-1.5 border border-slate-100/50">
                    <svg className="w-4 h-4 text-amber-500" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                    <span className="text-sm font-bold text-slate-800 tracking-tight">{liquor.averageRating?.toFixed(1) || '0.0'}</span>
                  </div>
                </div>

                {/* Content */}
                <div className="p-5 flex flex-col flex-grow">
                  <h3 className="text-lg font-bold text-slate-900 group-hover:text-amber-600 transition-colors mb-2 line-clamp-1">
                    {liquor.name}
                  </h3>
                  
                  <div className="mt-auto pt-4 border-t border-slate-100 flex items-center justify-between text-slate-500 text-sm font-medium">
                    <div className="flex items-center gap-4">
                      <div className="flex items-center gap-1.5 group/icon">
                        <svg className="w-4 h-4 text-slate-400 group-hover/icon:text-amber-500 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                        </svg>
                        <span className="group-hover/icon:text-slate-700 transition-colors">{liquor.reviewCount || 0}</span>
                      </div>
                      <div className="flex items-center gap-1.5 group/icon">
                        <svg className={`w-4 h-4 transition-colors ${liquor.likedByMe ? 'fill-red-500 text-red-500' : 'text-slate-400 group-hover/icon:text-red-400'}`} fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                        </svg>
                        <span className="group-hover/icon:text-slate-700 transition-colors">{liquor.likeCount || 0}</span>
                      </div>
                    </div>
                    
                    <div className="w-8 h-8 rounded-full bg-slate-50 flex items-center justify-center group-hover:bg-amber-100 group-hover:text-amber-600 transition-colors text-slate-400">
                      <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                      </svg>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
