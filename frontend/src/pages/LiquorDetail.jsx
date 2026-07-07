import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { fetchAuthToken } from '../utils/auth';
import CommentSection from '../components/CommentSection';
import { useTranslation } from 'react-i18next';

function ReviewCard({ review, currentUser, onUpdate, onDelete }) {
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const [showComments, setShowComments] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editRating, setEditRating] = useState(review.rating);
  const [editTitle, setEditTitle] = useState(review.title || '');
  const [editContent, setEditContent] = useState(review.content);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [showTranslation, setShowTranslation] = useState(false);
  const [translatedTitle, setTranslatedTitle] = useState('');
  const [translatedContent, setTranslatedContent] = useState('');
  const [isTranslating, setIsTranslating] = useState(false);

  const isKoreanText = (text) => /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/.test(text || '');

  const handleTranslate = async () => {
    if (translatedContent) {
      setShowTranslation(prev => !prev);
      return;
    }
    setIsTranslating(true);
    try {
      const response = await fetch(`/api/reviews/${review.id}/translate`);
      if (!response.ok) throw new Error('Translation failed');
      const data = await response.json();
      setTranslatedTitle(data.translatedTitle || '');
      setTranslatedContent(data.translatedContent || '');
      setShowTranslation(true);
    } catch (err) {
      console.error(err);
      window.alert('Translation failed.');
    } finally {
      setIsTranslating(false);
    }
  };

  const isOwner = currentUser && currentUser.id === review.userId;

  const handleDelete = async () => {
    if (!window.confirm(t('reviews.confirmDelete'))) return;
    try {
      const jwtData = await fetchAuthToken();
      const response = await fetch(`/api/reviews/${review.id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${jwtData.accessToken}` }
      });
      if (response.ok) {
        onDelete(review.id);
      } else {
        throw new Error("Failed to delete");
      }
    } catch (err) {
      console.error(err);
      window.alert(t('reviews.deleteError'));
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const jwtData = await fetchAuthToken();
      const response = await fetch(`/api/reviews/${review.id}`, {
        method: 'PATCH',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtData.accessToken}` 
        },
        body: JSON.stringify({
          rating: editRating,
          title: editTitle,
          content: editContent
        })
      });
      if (!response.ok) throw new Error("Failed to update");
      const updatedReview = await response.json();
      onUpdate(updatedReview);
      setIsEditing(false);
    } catch (err) {
      console.error(err);
      window.alert(t('reviews.updateError'));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLike = async () => {
    if (!currentUser) {
      window.alert(t('reviews.loginToLike'));
      navigate('/signin');
      return;
    }
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert(t('reviews.loginToLike'));
        navigate('/signin');
        return;
      }
      
      const response = await fetch(`/api/reviews/${review.id}/like`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });
      
      if (response.ok) {
        onUpdate({ ...review, likeCount: review.likeCount + 1 });
      } else if (response.status === 409) {
        const cancelRes = await fetch(`/api/reviews/${review.id}/cancel-like`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${jwtData.accessToken}`
          }
        });
        if (cancelRes.ok) {
          onUpdate({ ...review, likeCount: Math.max(0, review.likeCount - 1) });
        }
      } else {
        console.error("Failed to like review:", response.status);
      }
    } catch (err) {
      console.error(err);
    }
  };

  if (isEditing) {
    return (
      <div className="bg-white p-6 rounded-2xl border border-amber-300 shadow-md mb-4 animate-fade-in-up">
        <h3 className="font-bold text-lg mb-4 text-slate-800 flex items-center gap-2">
          <svg className="w-5 h-5 text-amber-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
          {t('reviews.editTitle')}
        </h3>
        <form onSubmit={handleUpdate} className="space-y-4">
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">{t('reviews.rating')}</label>
            <input 
              type="number" min="1" max="10" required
              value={editRating} onChange={e => setEditRating(Number(e.target.value))}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-2 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">{t('reviews.titleOptional')}</label>
            <input 
              type="text" 
              value={editTitle} onChange={e => setEditTitle(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-2 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">{t('reviews.content')}</label>
            <textarea 
              required rows="4"
              value={editContent} onChange={e => setEditContent(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-2 focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
            ></textarea>
          </div>
          <div className="flex gap-2 justify-end pt-2">
            <button type="button" onClick={() => setIsEditing(false)} className="px-5 py-2 text-slate-500 hover:bg-slate-100 rounded-xl font-semibold transition-colors">{t('common.cancel')}</button>
            <button type="submit" disabled={isSubmitting} className="px-6 py-2 bg-amber-500 hover:bg-amber-600 text-white rounded-xl font-bold transition-all disabled:opacity-50">{isSubmitting ? t('common.saving') : t('common.save')}</button>
          </div>
        </form>
      </div>
    );
  }

  return (
    <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm mb-4 transition-all">
      <div className="flex justify-between items-start mb-4">
        <div className="flex items-center gap-3">
          <img src={(review.userProfileImageUrl && !review.userProfileImageUrl.includes('default-profile')) ? review.userProfileImageUrl : '/default-avatar.svg'} alt="User Profile" className="w-10 h-10 rounded-full object-cover border border-slate-200 bg-white" />
          <div>
            <p className="font-bold text-slate-800">{review.username || t('reviews.anonymous')}</p>
            <div className="flex items-center gap-2">
              <span className="text-amber-500 text-sm font-bold">★ {review.rating}/10</span>
              <span className="text-slate-400 text-xs">{new Date(review.createdAt).toLocaleDateString()}</span>
              {review.updatedAt && review.updatedAt !== review.createdAt && (
                <span className="text-slate-400 text-[10px] italic">{t('reviews.edited')}</span>
              )}
            </div>
          </div>
        </div>
        
        {isOwner && (
          <div className="flex items-center gap-2">
            <button onClick={() => setIsEditing(true)} className="text-xs font-semibold text-slate-400 hover:text-amber-600 transition-colors bg-slate-50 hover:bg-amber-50 px-2.5 py-1.5 rounded-lg">
              {t('common.edit')}
            </button>
            <button onClick={handleDelete} className="text-xs font-semibold text-slate-400 hover:text-red-500 transition-colors bg-slate-50 hover:bg-red-50 px-2.5 py-1.5 rounded-lg">
              {t('common.delete')}
            </button>
          </div>
        )}
      </div>
      
      <h3 className="font-bold text-lg text-slate-800 mb-2">
        {showTranslation ? translatedTitle : review.title}
      </h3>
      <p className="text-slate-600 mb-2 whitespace-pre-wrap">
        {showTranslation ? translatedContent : review.content}
      </p>

      {(() => {
        const hasKorean = isKoreanText(review.content) || isKoreanText(review.title);
        const currentLang = i18n.language;
        const isDifferentLanguage = (hasKorean && currentLang !== 'ko') || (!hasKorean && currentLang === 'ko');

        if (!isDifferentLanguage) return null;

        return (
          <button 
            onClick={handleTranslate}
            disabled={isTranslating}
            className="text-xs font-bold text-amber-600 hover:text-amber-700 bg-amber-50 px-2.5 py-1.5 rounded-lg mb-4 transition-colors disabled:opacity-50 inline-flex items-center gap-1"
          >
            <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5h12M9 3v2m1.048 9.5A18.022 18.022 0 016.412 9m6.088 9h7M11 21l5-10 5 10M12.751 5C11.783 10.77 8.07 15.61 3 18.129" />
            </svg>
            {isTranslating ? t('common.loading') : showTranslation ? t('reviews.showOriginal') : t('reviews.translate')}
          </button>
        );
      })()}
      
      {review.imageUrls && review.imageUrls.length > 0 && (
        <div className="flex gap-2 overflow-x-auto mb-4 pb-2">
          {review.imageUrls.map((url, idx) => (
            <img key={idx} src={url} alt="Review attachment" className="h-32 w-32 object-cover rounded-xl border border-slate-200 flex-shrink-0 shadow-sm" />
          ))}
        </div>
      )}
      
      {review.tags && review.tags.length > 0 && (
        <div className="flex flex-wrap gap-2 mb-4">
          {review.tags.map(tag => (
            <span key={tag.id} className="text-xs font-semibold text-slate-500 bg-slate-100 px-2 py-1 rounded-md">
              #{tag.name}
            </span>
          ))}
        </div>
      )}

      <div className="flex items-center gap-4 pt-4 border-t border-slate-100">
        <button onClick={handleLike} className="flex items-center gap-1.5 text-slate-500 hover:text-red-500 transition-colors font-medium text-sm">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
          {review.likeCount || 0} {t('reviews.likes')}
        </button>
        <button 
          onClick={() => setShowComments(!showComments)}
          className="flex items-center gap-1.5 text-slate-500 hover:text-amber-600 transition-colors font-medium text-sm"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" /></svg>
          {review.commentCount || 0} {t('reviews.comments')}
        </button>
      </div>

      {showComments && (
        <CommentSection reviewId={review.id} initialCommentCount={review.commentCount || 0} currentUser={currentUser} />
      )}
    </div>
  );
}

function LiquorDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [liquor, setLiquor] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const [reviews, setReviews] = useState([]);
  const [reviewsCursor, setReviewsCursor] = useState(null);
  const [reviewsHasNext, setReviewsHasNext] = useState(false);
  const [reviewsLoading, setReviewsLoading] = useState(false);
  const [reviewSortBy, setReviewSortBy] = useState('REVIEW_ID');
  const [reviewSortDirection, setReviewSortDirection] = useState('DESC');

  const handleWriteReview = () => {
    if (localStorage.getItem('isLoggedIn') !== 'true') {
      window.alert(t('liquors.loginToReview'));
      navigate('/signin');
    } else {
      navigate(`/liquors/${id}/reviews/new`);
    }
  };

  const handleLike = async () => {
    if (localStorage.getItem('isLoggedIn') !== 'true') {
      window.alert(t('liquors.loginToLike'));
      navigate('/signin');
      return;
    }

    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) throw new Error('Failed to get auth token');
      
      const method = liquor.likedByMe ? 'DELETE' : 'POST';
      const endpoint = liquor.likedByMe ? 'cancel-like' : 'like';
      
      const response = await fetch(`/api/liquors/${id}/${endpoint}`, {
        method,
        headers: { 'Authorization': `Bearer ${jwtData.accessToken}` }
      });

      if (response.ok) {
        setLiquor(prev => ({
          ...prev,
          likedByMe: !prev.likedByMe,
          likeCount: prev.likedByMe ? prev.likeCount - 1 : prev.likeCount + 1
        }));
      } else {
        window.alert(t('common.error'));
      }
    } catch (err) {
      console.error(err);
      window.alert(t('common.error'));
    }
  };

  useEffect(() => {
    const fetchLiquorDetail = async () => {
      try {
        setIsLoading(true);
        
        let headers = {};
        if (localStorage.getItem('isLoggedIn') === 'true') {
          try {
            const jwtData = await fetchAuthToken();
            if (jwtData) {
              setCurrentUser(jwtData.userDto);
              headers['Authorization'] = `Bearer ${jwtData.accessToken}`;
            }
          } catch (e) {
            console.error('Failed to get auth token for fetch', e);
          }
        }
        
        const response = await fetch(`/api/liquors/${id}`, { headers });
        if (!response.ok) {
          if (response.status === 404) {
            throw new Error(t('liquors.notFound'));
          }
          throw new Error(t('common.error'));
        }
        
        const data = await response.json();
        setLiquor(data);
      } catch (err) {
        console.error(err);
        setError(err.message || t('common.error'));
      } finally {
        setIsLoading(false);
      }
    };

    if (id) {
      fetchLiquorDetail();
    }
  }, [id]);

  const fetchReviews = async (reset = false) => {
    try {
      if (reset) {
        setReviewsLoading(true);
      }
      const currentCursor = reset ? null : reviewsCursor;
      const cursorParam = currentCursor ? `&cursor=${currentCursor}` : '';
      
      const response = await fetch(
        `/api/liquors/${id}/reviews?sortBy=${reviewSortBy}&sortDirection=${reviewSortDirection}&limit=10${cursorParam}`
      );
      
      if (!response.ok) throw new Error('Failed to load reviews');
      const data = await response.json();
      
      if (reset) {
        setReviews(data.content);
      } else {
        setReviews(prev => [...prev, ...data.content]);
      }
      setReviewsCursor(data.nextCursor);
      setReviewsHasNext(data.hasNext);
    } catch (err) {
      console.error(err);
    } finally {
      setReviewsLoading(false);
    }
  };

  useEffect(() => {
    if (id) {
      fetchReviews(true);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id, reviewSortBy, reviewSortDirection]);

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
        <h2 className="text-3xl font-bold text-slate-800 mb-4">{error || t('liquors.notFound')}</h2>
        <button 
          onClick={() => navigate(-1)}
          className="bg-slate-800 hover:bg-slate-900 text-white px-6 py-3 rounded-xl font-medium transition-colors"
        >
          {t('liquors.goBack')}
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
        {t('liquors.backToLiquors')}
      </button>

      <div className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="flex flex-col md:flex-row">
          
          <div className="w-full md:w-2/5 bg-slate-50 relative flex items-center justify-center p-8 border-b md:border-b-0 md:border-r border-slate-200">
            {liquor.imageUrl ? (
              <img 
                src={liquor.imageUrl} 
                alt={liquor.name} 
                className="max-w-full max-h-[500px] object-contain drop-shadow-xl hover:scale-105 transition-transform duration-500"
                onError={(e) => { e.target.src = '/default-liquor.svg' }}
              />
            ) : (
              <img 
                src="/default-liquor.svg" 
                alt="Default Liquor" 
                className="max-w-full max-h-[500px] object-contain drop-shadow-xl hover:scale-105 transition-transform duration-500"
              />
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
                      {t('liquors.discontinued')}
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
                <span className="text-sm text-slate-500 mt-1 font-medium">{liquor.reviewCount} {t('common.reviews')}</span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-6 my-8 py-8 border-y border-slate-100">
              <div>
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">{t('liquors.country')}</p>
                <p className="text-lg font-medium text-slate-800 flex items-center">
                  <span className="mr-2">{liquor.countryName === '대한민국' ? '🇰🇷' : liquor.countryName === '미국' ? '🇺🇸' : liquor.countryName === '프랑스' ? '🇫🇷' : liquor.countryName === '일본' ? '🇯🇵' : liquor.countryName === '영국' ? '🇬🇧' : '🌍'}</span>
                  {liquor.countryName || t('common.unknown')}
                </p>
              </div>
              <div>
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">{t('liquors.abv')}</p>
                <p className="text-lg font-medium text-slate-800">{liquor.abv}%</p>
              </div>
              <div>
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">{t('liquors.manufacturer')}</p>
                <p className="text-lg font-medium text-slate-800">{liquor.manufacturer || t('common.unknown')}</p>
              </div>
              {liquor.subcategoryName && (
                <div>
                  <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-1">{t('liquors.style')}</p>
                  <p className="text-lg font-medium text-slate-800">{liquor.subcategoryName}</p>
                </div>
              )}
            </div>

            {/* Tags */}
            {liquor.tags && liquor.tags.length > 0 && (
              <div className="mb-8">
                <p className="text-sm text-slate-400 uppercase font-semibold tracking-wider mb-3">{t('liquors.tastingNotes')}</p>
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
              <div className="flex items-center text-slate-500 font-medium gap-2">
                <button 
                  onClick={handleLike}
                  className={`p-2 rounded-xl transition-all border ${
                    liquor.likedByMe 
                      ? 'bg-rose-50 border-rose-100 text-rose-500 hover:bg-rose-100' 
                      : 'bg-white border-slate-200 text-slate-400 hover:text-rose-500 hover:bg-slate-50'
                  }`}
                  title={liquor.likedByMe ? "Unlike" : "Like"}
                >
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                     <path fillRule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clipRule="evenodd" />
                  </svg>
                </button>
                <span className="text-base font-bold text-slate-700">{liquor.likeCount}</span>
              </div>
              
              <button 
                onClick={handleWriteReview}
                className="bg-amber-500 hover:bg-amber-600 text-white font-bold py-3.5 px-8 rounded-xl transition-all duration-200 shadow-sm shadow-amber-500/30 flex items-center gap-2 hover:-translate-y-1"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path></svg>
                {t('liquors.writeReview')}
              </button>
            </div>

          </div>
        </div>
      </div>

      {/* Reviews Section */}
      <div className="mt-16">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-extrabold text-slate-900">{t('reviews.title')}</h2>
          <select 
            value={`${reviewSortBy}-${reviewSortDirection}`}
            onChange={(e) => {
              const [newSortBy, newSortDir] = e.target.value.split('-');
              setReviewSortBy(newSortBy);
              setReviewSortDirection(newSortDir);
            }}
            className="border border-slate-200 bg-white text-slate-700 rounded-xl py-2 px-4 focus:ring-4 focus:ring-amber-500/10 focus:border-amber-400 outline-none cursor-pointer font-medium shadow-sm transition-all"
          >
            <option value="REVIEW_ID-DESC">{t('reviews.sort.latest')}</option>
            <option value="REVIEW_ID-ASC">{t('reviews.sort.oldest')}</option>
            <option value="LIKE_COUNT-DESC">{t('reviews.sort.mostLiked')}</option>
            <option value="COMMENT_COUNT-DESC">{t('reviews.sort.mostDiscussed')}</option>
          </select>
        </div>

        {reviewsLoading && reviews.length === 0 ? (
          <div className="flex justify-center py-12">
            <svg className="animate-spin h-8 w-8 text-amber-500" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
          </div>
        ) : reviews.length === 0 ? (
          <div className="text-center py-16 bg-white rounded-3xl border border-slate-200 shadow-sm">
            <div className="text-5xl mb-4">✍️</div>
            <h3 className="text-xl font-bold text-slate-800 mb-2">{t('reviews.noReviews')}</h3>
            <p className="text-slate-500 mb-6">{t('reviews.noReviewsDesc')}</p>
            <button 
              onClick={handleWriteReview}
              className="bg-amber-500 hover:bg-amber-600 text-white font-bold py-3 px-6 rounded-xl transition-all shadow-md"
            >
              {t('liquors.writeReview')}
            </button>
          </div>
        ) : (
          <div className="space-y-6">
            {reviews.map(review => (
              <ReviewCard 
                key={review.id} 
                review={review} 
                currentUser={currentUser}
                onUpdate={(updatedReview) => {
                  setReviews(prev => prev.map(r => r.id === updatedReview.id ? updatedReview : r));
                }}
                onDelete={(reviewId) => {
                  setReviews(prev => prev.filter(r => r.id !== reviewId));
                  setLiquor(prev => ({...prev, reviewCount: Math.max(0, prev.reviewCount - 1)}));
                }}
              />
            ))}
            
            {reviewsHasNext && (
              <div className="text-center pt-4">
                <button 
                  onClick={() => fetchReviews(false)}
                  className="bg-white border border-slate-200 hover:border-amber-400 text-slate-700 hover:text-amber-600 font-bold py-3 px-8 rounded-xl transition-all shadow-sm"
                >
                  {t('reviews.loadMore')}
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default LiquorDetail;
