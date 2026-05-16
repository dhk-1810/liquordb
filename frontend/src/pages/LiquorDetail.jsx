import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';

function LiquorDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [liquor, setLiquor] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchLiquorDetail = async () => {
      try {
        setIsLoading(true);
        // Include authorization header if user is logged in
        const token = localStorage.getItem('isLoggedIn') === 'true' ? true : false;
        // In this app, auth is handled by cookies/session or token refresh handled centrally? 
        // Let's just make a standard fetch, if it's cookie based it will be sent automatically.
        // Wait, looking at App.jsx, it seems it might use cookies for refresh, but Bearer tokens for API?
        // App.jsx line 33: headers: { 'Authorization': `Bearer ${jwtData.accessToken}` }
        // For public endpoints, we might not need it, or we should get it.
        // Let's just fetch without auth header for now, GET /api/liquors/{id} allows anonymous (returns likedByMe: false).
        
        const response = await fetch(`/api/liquors/${id}`);
        if (!response.ok) {
          if (response.status === 404) {
            throw new Error('Liquor not found');
          }
          throw new Error('Failed to fetch liquor details');
        }
        
        const data = await response.json();
        setLiquor(data);
      } catch (err) {
        console.error(err);
        setError(err.message || 'An error occurred while fetching details.');
      } finally {
        setIsLoading(false);
      }
    };

    if (id) {
      fetchLiquorDetail();
    }
  }, [id]);

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 flex justify-center items-center">
        <svg className="animate-spin h-12 w-12 text-amber-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
    );
  }

  if (error || !liquor) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 text-center animate-fade-in-up">
        <div className="text-6xl mb-6">😕</div>
        <h2 className="text-3xl font-bold text-slate-800 mb-4">{error || 'Liquor not found'}</h2>
        <button 
          onClick={() => navigate(-1)}
          className="bg-slate-800 hover:bg-slate-900 text-white px-6 py-3 rounded-xl font-medium transition-colors"
        >
          Go Back
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      <button 
        onClick={() => navigate(-1)}
        className="mb-8 flex items-center text-slate-500 hover:text-amber-600 font-medium transition-colors group"
      >
        <svg className="w-5 h-5 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        Back to Liquors
      </button>

      <div className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="flex flex-col md:flex-row">
          
          {/* Image Section */}
          <div className="w-full md:w-2/5 bg-slate-50 relative flex items-center justify-center p-8 border-b md:border-b-0 md:border-r border-slate-200">
            {liquor.imageUrl ? (
              <img 
                src={liquor.imageUrl} 
                alt={liquor.name} 
                className="max-w-full max-h-[500px] object-contain drop-shadow-xl hover:scale-105 transition-transform duration-500"
              />
            ) : (
              <div className="w-64 h-64 flex flex-col items-center justify-center text-amber-300">
                <svg className="w-32 h-32 mb-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M21 16.5c0 .38-.21.71-.53.88l-7.9 4.44c-.16.12-.36.18-.57.18s-.41-.06-.57-.18l-7.9-4.44a.991.991 0 01-.53-.88V7.5c0-.38.21-.71.53-.88l7.9-4.44c.16-.12.36-.18.57-.18s.41.06.57.18l7.9 4.44c.32.17.53.5.53.88v9z" />
                </svg>
                <span className="text-slate-400 font-medium">No image available</span>
              </div>
            )}
            
            {liquor.likedByMe && (
               <div className="absolute top-6 right-6 bg-white/90 backdrop-blur text-red-500 p-2.5 rounded-full shadow-md">
                  <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                     <path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" />
                  </svg>
               </div>
            )}
          </div>

          {/* Details Section */}
          <div className="w-full md:w-3/5 p-8 md:p-12 flex flex-col">
            <div className="flex justify-between items-start mb-4">
              <div>
                <div className="flex gap-2 items-center mb-3">
                  <span className="px-3 py-1 bg-amber-100 text-amber-800 text-xs font-bold uppercase tracking-wider rounded-full">
                    {liquor.category}
                  </span>
                  {liquor.isDiscontinued && (
                    <span className="px-3 py-1 bg-red-100 text-red-800 text-xs font-bold uppercase tracking-wider rounded-full">
                      Discontinued
                    </span>
                  )}
                </div>
                <h1 className="text-4xl md:text-5xl font-extrabold text-slate-900 tracking-tight leading-tight mb-2">
                  {liquor.name}
                </h1>
              </div>
              <div className="flex flex-col items-end">
                <div className="flex items-center gap-1.5 bg-slate-50 px-4 py-2 rounded-2xl border border-slate-100 shadow-sm">
                  <svg className="w-5 h-5 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                  </svg>
                  <span className="text-xl font-bold text-slate-800">{liquor.averageRating ? liquor.averageRating.toFixed(1) : 'N/A'}</span>
                </div>
                <span className="text-sm text-slate-500 mt-1 font-medium">{liquor.reviewCount} reviews</span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-6 my-8 py-8 border-y border-slate-100">
              <div>
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">Country</p>
                <p className="text-lg font-medium text-slate-800 flex items-center">
                  <span className="mr-2">{liquor.countryName === '대한민국' ? '🇰🇷' : liquor.countryName === '미국' ? '🇺🇸' : liquor.countryName === '프랑스' ? '🇫🇷' : liquor.countryName === '일본' ? '🇯🇵' : liquor.countryName === '영국' ? '🇬🇧' : '🌍'}</span>
                  {liquor.countryName || 'Unknown'}
                </p>
              </div>
              <div>
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">ABV</p>
                <p className="text-lg font-medium text-slate-800">{liquor.abv}%</p>
              </div>
              <div>
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">Manufacturer</p>
                <p className="text-lg font-medium text-slate-800">{liquor.manufacturer || 'Unknown'}</p>
              </div>
              {liquor.subcategoryName && (
                <div>
                  <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">Style</p>
                  <p className="text-lg font-medium text-slate-800">{liquor.subcategoryName}</p>
                </div>
              )}
            </div>

            {/* Tags */}
            {liquor.tags && liquor.tags.length > 0 && (
              <div className="mb-8">
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-3">Tasting Notes & Tags</p>
                <div className="flex flex-wrap gap-2">
                  {liquor.tags.map((tag, idx) => (
                    <span key={idx} className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-sm font-medium rounded-lg transition-colors cursor-default">
                      #{tag.name}
                    </span>
                  ))}
                </div>
              </div>
            )}

            <div className="mt-auto pt-6 flex items-center justify-between">
              <div className="flex items-center text-slate-500 font-medium">
                <div className="flex -space-x-2 mr-3">
                  {/* Fake avatars for people who liked this */}
                  <div className="w-8 h-8 rounded-full border-2 border-white bg-blue-100 flex items-center justify-center text-blue-500 text-xs font-bold">A</div>
                  <div className="w-8 h-8 rounded-full border-2 border-white bg-green-100 flex items-center justify-center text-green-500 text-xs font-bold">B</div>
                  <div className="w-8 h-8 rounded-full border-2 border-white bg-amber-100 flex items-center justify-center text-amber-500 text-xs font-bold">C</div>
                </div>
                <span>{liquor.likeCount} people liked this</span>
              </div>
              
              <button className="bg-amber-500 hover:bg-amber-600 text-white font-bold py-3.5 px-8 rounded-xl transition-all duration-200 shadow-sm shadow-amber-500/30 flex items-center gap-2 hover:-translate-y-1">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path></svg>
                Write Review
              </button>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}

export default LiquorDetail;
