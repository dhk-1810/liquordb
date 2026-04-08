import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

function LiquorsExplore() {
  const [liquors, setLiquors] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [sortBy, setSortBy] = useState('LIQUOR_ID');
  const [category, setCategory] = useState('');
  const [nextCursor, setNextCursor] = useState(null);
  const [hasNext, setHasNext] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchLiquors = async (reset = false) => {
    try {
      setIsLoading(true);
      setError(null);
      
      const params = new URLSearchParams({
        limit: '12',
        sortBy: sortBy,
        // Using sortDirection as DESC by default for all unless we add a UI toggle.
        sortDirection: 'DESC'
      });
      
      if (keyword) params.append('keyword', keyword);
      if (category) params.append('category', category);
      if (!reset && nextCursor) params.append('cursor', nextCursor);

      const queryUrl = `/api/liquors?${params.toString()}`;
      
      const response = await fetch(queryUrl);
      if (!response.ok) {
        throw new Error('Failed to fetch liquors');
      }
      
      const data = await response.json();
      
      if (reset) {
        setLiquors(data.content || []);
      } else {
        setLiquors(prev => [...prev, ...(data.content || [])]);
      }
      
      setNextCursor(data.nextCursor);
      setHasNext(data.hasNext);
    } catch (err) {
      console.error(err);
      setError('An error occurred while fetching liquors.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchLiquors(true);
  }, [category, sortBy]); // Fetch automatically when category or sort changes

  const handleSearchClick = (e) => {
    e.preventDefault();
    fetchLiquors(true);
  };

  const handleLoadMore = () => {
    fetchLiquors(false);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fade-in-up">
      <div className="mb-8">
        <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">Explore Liquors</h1>
        <p className="text-lg text-slate-600">Discover and search for your favorite drinks.</p>
      </div>

      {/* Filters and Search Bar */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-5 mb-10">
        <form onSubmit={handleSearchClick} className="flex flex-col md:flex-row gap-4 items-center">
          
          <div className="flex-grow w-full md:w-auto relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
            <input
              type="text"
              placeholder="Search by name..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all duration-200"
            />
          </div>

          <div className="w-full md:w-48">
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-200 text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all duration-200 appearance-none font-medium"
            >
              <option value="">All Categories</option>
              <option value="BEER">Beer</option>
              <option value="WINE">Wine</option>
              <option value="WHISKY">Whisky</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div className="w-full md:w-48">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-200 text-slate-700 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all duration-200 appearance-none font-medium"
            >
              <option value="LIQUOR_ID">Newest Responses</option>
              <option value="LIKE_COUNT">Most Liked</option>
              <option value="AVERAGE_RATING">Highest Rating</option>
            </select>
          </div>

          <button
            type="submit"
            className="w-full md:w-auto bg-amber-500 hover:bg-amber-600 text-white font-bold py-3 px-6 rounded-xl transition-all duration-200 shadow-sm shadow-amber-500/30 whitespace-nowrap"
          >
            Search
          </button>
        </form>
      </div>

      {error && (
        <div className="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium">
          {error}
        </div>
      )}

      {/* Grid */}
      {liquors.length === 0 && !isLoading ? (
        <div className="text-center py-20 bg-white rounded-3xl border border-dashed border-slate-300">
          <div className="text-5xl mb-4">🍷</div>
          <h3 className="text-xl font-bold text-slate-700 mb-2">No liquors found</h3>
          <p className="text-slate-500">Try adjusting your search criteria or categories.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {liquors.map((liquor) => (
            <Link to={`/liquors/${liquor.id}`} key={liquor.id} className="group flex flex-col bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 border border-slate-100 transform hover:-translate-y-1">
              <div className="relative aspect-[4/3] bg-slate-100 overflow-hidden">
                {liquor.imageUrl ? (
                  <img
                    src={liquor.imageUrl}
                    alt={liquor.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-amber-50 to-orange-50 text-amber-300 group-hover:scale-105 transition-transform duration-500">
                    <svg className="w-16 h-16" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M21 16.5c0 .38-.21.71-.53.88l-7.9 4.44c-.16.12-.36.18-.57.18s-.41-.06-.57-.18l-7.9-4.44a.991.991 0 01-.53-.88V7.5c0-.38.21-.71.53-.88l7.9-4.44c.16-.12.36-.18.57-.18s.41.06.57.18l7.9 4.44c.32.17.53.5.53.88v9z" />
                    </svg>
                  </div>
                )}
                {/* Overlay gradient */}
                <div className="absolute inset-0 bg-gradient-to-t from-slate-900/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                
                {/* Heart / likedByMe indicator */}
                {liquor.likedByMe && (
                   <div className="absolute top-3 right-3 bg-white/90 backdrop-blur text-red-500 p-1.5 rounded-full shadow-sm">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                         <path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" />
                      </svg>
                   </div>
                )}
              </div>
              <div className="p-5 flex flex-col flex-grow">
                <h3 className="text-lg font-bold text-slate-800 mb-1 group-hover:text-amber-600 transition-colors line-clamp-1">{liquor.name}</h3>
                
                <div className="flex items-center gap-1.5 mb-4">
                  <svg className="w-4 h-4 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                  </svg>
                  <span className="font-semibold text-slate-700">{liquor.averageRating ? liquor.averageRating.toFixed(1) : 'No rating'}</span>
                  <span className="text-xs text-slate-400">({liquor.reviewCount} reviews)</span>
                </div>
                
                <div className="mt-auto flex items-center justify-between text-sm text-slate-500 font-medium">
                  <span className="flex items-center gap-1">
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path></svg>
                    {liquor.likeCount} likes
                  </span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* Loading state / Load more */}
      <div className="mt-12 text-center">
        {isLoading ? (
          <div className="inline-flex items-center justify-center">
            <svg className="animate-spin -ml-1 mr-3 h-8 w-8 text-amber-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
        ) : hasNext && (
          <button
            onClick={handleLoadMore}
            className="bg-white border-2 border-slate-200 hover:border-amber-500 hover:text-amber-600 text-slate-600 font-bold py-3.5 px-8 rounded-full transition-all tracking-wide"
          >
            Load More Liquors
          </button>
        )}
      </div>
    </div>
  );
}

export default LiquorsExplore;
