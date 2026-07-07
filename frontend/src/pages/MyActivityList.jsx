import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { fetchAuthToken } from '../utils/auth';

function MyActivityList() {
  const { category } = useParams(); // 'liked-liquors', 'liked-reviews', 'reviews', 'comments'
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [nextCursor, setNextCursor] = useState(null);
  const [hasNext, setHasNext] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const getTitle = () => {
    switch (category) {
      case 'liked-liquors': return 'Liked Liquors';
      case 'liked-reviews': return 'Liked Reviews';
      case 'reviews': return 'Reviews Written';
      case 'comments': return 'Comments Written';
      default: return 'My Activity';
    }
  };

  const getEndpoint = (userId) => {
    switch (category) {
      case 'liked-liquors': return `/api/users/${userId}/liked-liquors`;
      case 'liked-reviews': return `/api/users/${userId}/liked-reviews`;
      case 'reviews': return `/api/users/${userId}/reviews`;
      case 'comments': return `/api/users/${userId}/comments`;
      default: return '';
    }
  };

  const getEmptyMessage = () => {
    switch (category) {
      case 'liked-liquors': return "You haven't liked any liquors yet.";
      case 'liked-reviews': return "You haven't liked any reviews yet.";
      case 'reviews': return "You haven't written any reviews yet.";
      case 'comments': return "You haven't written any comments yet.";
      default: return "No records found.";
    }
  };

  const fetchItems = async (reset = false) => {
    try {
      setIsLoading(true);
      setError(null);

      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        setError('Please log in to view this page.');
        navigate('/signin');
        return;
      }

      const endpoint = getEndpoint(jwtData.userDto.id);
      if (!endpoint) {
        setError('Invalid category.');
        return;
      }

      const params = new URLSearchParams({
        limit: category === 'liked-liquors' ? '12' : '10'
      });
      if (!reset && nextCursor) {
        params.append('cursor', nextCursor);
      }

      const response = await fetch(`${endpoint}?${params.toString()}`, {
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });

      if (!response.ok) {
        // If there's no record or query fails, treat it as empty list instead of showing error banner
        if (reset) {
          setItems([]);
        }
        setNextCursor(null);
        setHasNext(false);
        return;
      }

      const data = await response.json();
      const contentList = data.content || [];

      if (reset) {
        setItems(contentList);
      } else {
        setItems(prev => [...prev, ...contentList]);
      }

      setNextCursor(data.nextCursor);
      setHasNext(data.hasNext);
    } catch (err) {
      console.error(err);
      // Do not set error banner to keep UI clean if no record exists
      if (reset) {
        setItems([]);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    setItems([]);
    setNextCursor(null);
    setHasNext(false);
    fetchItems(true);
  }, [category]);

  const handleLoadMore = () => {
    fetchItems(false);
  };

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      <button
        onClick={() => navigate('/mypage')}
        className="mb-8 flex items-center text-slate-500 hover:text-amber-600 font-medium transition-colors group"
      >
        <svg className="w-5 h-5 mr-2 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        Back to My Page
      </button>

      <div className="mb-10">
        <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">{getTitle()}</h1>
        <p className="text-slate-500 font-medium">Browse and manage your activity history.</p>
      </div>

      {error && (
        <div className="mb-8 bg-red-50 border border-red-200 text-red-600 px-5 py-4 rounded-2xl text-sm font-semibold shadow-sm">
          {error}
        </div>
      )}

      {/* Content Rendering based on Category */}
      {items.length === 0 && !isLoading ? (
        <div className="text-center py-20 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <div className="text-6xl mb-6">📂</div>
          <h3 className="text-xl font-bold text-slate-800 mb-2">No records found</h3>
          <p className="text-slate-400">{getEmptyMessage()}</p>
        </div>
      ) : (
        <>
          {category === 'liked-liquors' && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {items.map(liquor => (
                <Link to={`/liquors/${liquor.id}`} key={liquor.id} className="group flex flex-col bg-white rounded-2xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 border border-slate-200 transform hover:-translate-y-1">
                  <div className="relative aspect-[4/3] bg-slate-100 overflow-hidden">
                    {liquor.imageUrl ? (
                      <img 
                        src={liquor.imageUrl} 
                        alt={liquor.name} 
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                        onError={(e) => { e.target.src = '/default-liquor.svg' }}
                      />
                    ) : (
                      <img 
                        src="/default-liquor.svg" 
                        alt={liquor.name} 
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                      />
                    )}
                    <div className="absolute top-3 right-3 bg-white/90 backdrop-blur text-red-500 p-1.5 rounded-full shadow-sm">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" />
                      </svg>
                    </div>
                  </div>
                  <div className="p-5 flex flex-col flex-grow">
                    <span className="px-2 py-0.5 bg-amber-50 text-amber-700 text-[10px] font-extrabold uppercase tracking-wider rounded-md self-start mb-2 border border-amber-100">
                      {liquor.category}
                    </span>
                    <h3 className="text-lg font-bold text-slate-800 mb-1 group-hover:text-amber-600 transition-colors line-clamp-1">{liquor.name}</h3>
                    <div className="flex items-center gap-1.5 mb-2">
                      <svg className="w-4 h-4 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                      <span className="font-semibold text-slate-700 text-sm">{liquor.averageRating ? liquor.averageRating.toFixed(1) : 'No rating'}</span>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          )}

          {(category === 'liked-reviews' || category === 'reviews') && (
            <div className="space-y-6">
              {items.map(review => (
                <div key={review.id} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm transition-all">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex items-center gap-3">
                      <img src="/default-avatar.svg" alt="User Profile" className="w-10 h-10 rounded-full object-cover border border-slate-200 bg-white" />
                      <div>
                        <p className="font-bold text-slate-800">{review.username || 'Anonymous'}</p>
                        <div className="flex items-center gap-2">
                          <span className="text-amber-500 text-sm font-bold">★ {review.rating}/10</span>
                          <span className="text-slate-400 text-xs">{new Date(review.createdAt).toLocaleDateString()}</span>
                        </div>
                      </div>
                    </div>
                    <Link to={`/liquors/${review.liquorId}`} className="text-xs font-semibold text-amber-600 hover:text-amber-700 bg-amber-50 px-3 py-1.5 rounded-lg border border-amber-100 transition-colors">
                      View Liquor →
                    </Link>
                  </div>
                  <h3 className="font-bold text-lg text-slate-800 mb-2">{review.title}</h3>
                  <p className="text-slate-600 mb-4 whitespace-pre-wrap">{review.content}</p>

                  {review.imageUrls && review.imageUrls.length > 0 && (
                    <div className="flex gap-2 overflow-x-auto mb-4 pb-2">
                      {review.imageUrls.map((url, idx) => (
                        <img key={idx} src={url} alt="Review attachment" className="h-24 w-24 object-cover rounded-xl border border-slate-200 flex-shrink-0 shadow-sm" />
                      ))}
                    </div>
                  )}

                  {review.tags && review.tags.length > 0 && (
                    <div className="flex flex-wrap gap-2 mb-4">
                      {review.tags.map(tag => (
                        <span key={tag.id} className="text-xs font-semibold text-slate-500 bg-slate-100 px-2.5 py-1 rounded-md">
                          #{tag.name}
                        </span>
                      ))}
                    </div>
                  )}
                  <div className="flex items-center gap-4 pt-4 border-t border-slate-100 text-slate-500 text-sm font-medium">
                    <span className="flex items-center gap-1.5">
                      <svg className="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path></svg>
                      {review.likeCount || 0} Likes
                    </span>
                    <span className="flex items-center gap-1.5">
                      <svg className="w-4.5 h-4.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path></svg>
                      {review.commentCount || 0} Comments
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}

          {category === 'comments' && (
            <div className="space-y-4">
              {items.map(comment => (
                <div key={comment.id} className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:border-amber-300 transition-all duration-300 flex items-start gap-4">
                  <img src="/default-avatar.svg" alt="User Profile" className="w-8 h-8 rounded-full object-cover border border-slate-200 bg-white mt-1" />
                  <div className="flex-grow">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1 mb-2">
                      <p className="text-xs text-slate-500 font-medium">
                        Commented on review: <span className="font-bold text-slate-700">"{comment.reviewTitle || 'Untitled Review'}"</span>
                      </p>
                      <span className="text-[10px] text-slate-400 font-semibold">{new Date(comment.createdAt).toLocaleString()}</span>
                    </div>
                    <p className="text-slate-800 text-sm whitespace-pre-wrap leading-relaxed">{comment.content}</p>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Load More Pagination */}
          {hasNext && (
            <div className="mt-12 text-center">
              {isLoading ? (
                <svg className="animate-spin h-8 w-8 text-amber-500 mx-auto" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : (
                <button
                  onClick={handleLoadMore}
                  className="bg-white border-2 border-slate-200 hover:border-amber-500 hover:text-amber-600 text-slate-600 font-bold py-3 px-8 rounded-full transition-all text-sm tracking-wide shadow-sm"
                >
                  Load More Activity
                </button>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default MyActivityList;
