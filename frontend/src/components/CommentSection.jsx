import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchAuthToken } from '../utils/auth';
import { useTranslation } from 'react-i18next';

function CommentSection({ reviewId, initialCommentCount, currentUser, onCommentCountChange }) {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const [comments, setComments] = useState([]);
  const [cursor, setCursor] = useState(null);
  const [hasNext, setHasNext] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const [sortBy, setSortBy] = useState('COMMENT_ID');
  const [sortDirection, setSortDirection] = useState('DESC');
  
  const [newComment, setNewComment] = useState('');
  const [commentCount, setCommentCount] = useState(initialCommentCount || 0);

  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editContent, setEditContent] = useState('');

  const isKoreanText = (text) => /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/.test(text || '');

  const handleTranslate = async (commentId) => {
    const comment = comments.find(c => c.id === commentId);
    if (!comment) return;

    if (comment.translatedContent) {
      setComments(prev => prev.map(c => c.id === commentId ? { ...c, showTranslation: !c.showTranslation } : c));
      return;
    }

    setComments(prev => prev.map(c => c.id === commentId ? { ...c, isTranslating: true } : c));

    try {
      const response = await fetch(`/api/comments/${commentId}/translate`);
      if (!response.ok) throw new Error('Translation failed');
      const data = await response.json();
      
      setComments(prev => prev.map(c => c.id === commentId ? { 
        ...c, 
        translatedContent: data.translatedContent || '', 
        showTranslation: true,
        isTranslating: false 
      } : c));
    } catch (err) {
      console.error(err);
      window.alert('Translation failed.');
      setComments(prev => prev.map(c => c.id === commentId ? { ...c, isTranslating: false } : c));
    }
  };

  const loadComments = async (reset = false) => {
    try {
      if (reset) {
        setIsLoading(true);
      }
      const currentCursor = reset ? null : cursor;
      const cursorParam = currentCursor ? `&cursor=${currentCursor}` : '';
      
      const response = await fetch(
        `/api/reviews/${reviewId}/comments?sortBy=${sortBy}&sortDirection=${sortDirection}&limit=10${cursorParam}`
      );
      
      if (!response.ok) throw new Error('Failed to load comments');
      const data = await response.json();
      
      if (reset) {
        setComments(data.content);
      } else {
        setComments(prev => [...prev, ...data.content]);
      }
      
      setCursor(data.nextCursor);
      setHasNext(data.hasNext);
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadComments(true);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [reviewId, sortBy, sortDirection]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    
    setIsSubmitting(true);
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert(t('comments.loginToComment'));
        navigate('/signin');
        return;
      }

      const response = await fetch(`/api/reviews/${reviewId}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtData.accessToken}`
        },
        body: JSON.stringify({ content: newComment })
      });

      if (!response.ok) throw new Error('Failed to post comment');
      
      // Reload from top
      setNewComment('');
      setCommentCount(prev => {
        const next = prev + 1;
        if (onCommentCountChange) onCommentCountChange(next);
        return next;
      });
      
      // If we are sorting by latest, it will appear at the top. 
      // Force reload of first page:
      if (sortBy === 'COMMENT_ID' && sortDirection === 'DESC') {
        loadComments(true);
      } else {
        // Switch to latest so user sees their comment
        setSortBy('COMMENT_ID');
        setSortDirection('DESC');
      }
      
    } catch (err) {
      console.error(err);
      window.alert(t('comments.errorPosting'));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLike = async (comment) => {
    if (!currentUser) {
      window.alert(t('comments.loginToLike'));
      navigate('/signin');
      return;
    }

    if (currentUser.id === comment.userId) {
      window.alert(t('comments.selfLikeNotAllowed', '본인이 작성한 댓글에는 좋아요를 누를 수 없습니다.'));
      return;
    }

    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert(t('comments.loginToLike'));
        navigate('/signin');
        return;
      }
      
      const method = comment.likedByMe ? 'DELETE' : 'POST';
      const endpoint = comment.likedByMe ? 'cancel-like' : 'like';

      const response = await fetch(`/api/comments/${comment.id}/${endpoint}`, {
        method,
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });
      
      if (response.ok) {
        const nextLikedByMe = !comment.likedByMe;
        const nextLikeCount = nextLikedByMe ? comment.likeCount + 1 : Math.max(0, comment.likeCount - 1);
        setComments(prev => prev.map(c => {
          if (c.id === comment.id) {
            return { ...c, likedByMe: nextLikedByMe, likeCount: nextLikeCount };
          }
          return c;
        }));
      } else if (response.status === 409) {
        const errData = await response.json().catch(() => ({}));
        window.alert(errData.message || t('comments.alreadyLiked', '이미 좋아요 한 댓글이거나 처리 중 오류가 발생했습니다.'));
      } else if (response.status === 400) {
        const errData = await response.json().catch(() => ({}));
        window.alert(errData.message || t('comments.selfLikeNotAllowed', '본인이 작성한 댓글에는 좋아요를 누를 수 없습니다.'));
      } else {
        console.error("Failed to like/unlike comment:", response.status);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (commentId) => {
    if (!window.confirm(t('comments.deleteConfirm'))) return;
    try {
      const jwtData = await fetchAuthToken();
      const response = await fetch(`/api/comments/${commentId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${jwtData.accessToken}` }
      });
      if (response.ok) {
        setComments(prev => prev.filter(c => c.id !== commentId));
        setCommentCount(prev => {
          const next = Math.max(0, prev - 1);
          if (onCommentCountChange) onCommentCountChange(next);
          return next;
        });
      } else {
        throw new Error("Failed to delete comment");
      }
    } catch (err) {
      console.error(err);
      window.alert(t('comments.errorDeleting'));
    }
  };

  const handleUpdate = async (commentId) => {
    if (!editContent.trim()) return;
    try {
      const jwtData = await fetchAuthToken();
      const response = await fetch(`/api/comments/${commentId}`, {
        method: 'PATCH',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtData.accessToken}` 
        },
        body: JSON.stringify({ content: editContent })
      });
      if (!response.ok) throw new Error("Failed to update comment");
      const updatedComment = await response.json();
      setComments(prev => prev.map(c => c.id === commentId ? updatedComment : c));
      setEditingCommentId(null);
    } catch (err) {
      console.error(err);
      window.alert(t('comments.errorUpdating'));
    }
  };

  const startEdit = (comment) => {
    setEditingCommentId(comment.id);
    setEditContent(comment.content);
  };
  return (
    <div className="mt-6 pt-6 border-t border-slate-100 animate-fade-in-up">
      <div className="flex items-center justify-between mb-6">
        <h4 className="font-bold text-slate-800 text-lg flex items-center gap-2">
          {t('comments.title')}
          <span className="bg-slate-100 text-slate-500 text-sm py-0.5 px-2 rounded-full">{commentCount}</span>
        </h4>
        <select 
          value={`${sortBy}-${sortDirection}`}
          onChange={(e) => {
            const [newSortBy, newSortDir] = e.target.value.split('-');
            setSortBy(newSortBy);
            setSortDirection(newSortDir);
          }}
          className="text-sm border-none bg-slate-50 text-slate-600 rounded-lg py-1.5 px-3 focus:ring-0 cursor-pointer font-medium"
        >
          <option value="COMMENT_ID-DESC">{t('reviews.sort.latest')}</option>
          <option value="COMMENT_ID-ASC">{t('reviews.sort.oldest')}</option>
          <option value="LIKE_COUNT-DESC">{t('reviews.sort.mostLiked')}</option>
        </select>
      </div>

      {/* Comment Input */}
      <form onSubmit={handleSubmit} className="mb-8 flex gap-3">
        <img src={(currentUser && currentUser.profileImageUrl && !currentUser.profileImageUrl.includes('default-profile')) ? currentUser.profileImageUrl : '/default-avatar.svg'} alt="User Profile" className="w-8 h-8 rounded-full object-cover border border-slate-200 bg-white flex-shrink-0" />
        <div className="flex-grow">
          <input 
            type="text" 
            placeholder={t('comments.placeholder')} 
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            onFocus={(e) => {
              if (!currentUser) {
                e.preventDefault();
                window.alert(t('comments.loginToComment'));
                navigate('/signin');
              }
            }}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-2.5 text-sm focus:bg-white focus:border-amber-400 focus:ring-4 focus:ring-amber-500/10 outline-none transition-all"
          />
          {newComment.trim() && (
            <div className="mt-2 flex justify-end">
              <button 
                type="submit" 
                disabled={isSubmitting}
                className="bg-amber-500 hover:bg-amber-600 text-white text-sm font-bold py-1.5 px-4 rounded-lg transition-colors"
              >
                {isSubmitting ? t('comments.posting') : t('comments.post')}
              </button>
            </div>
          )}
        </div>
      </form>

      {/* Comments List */}
      {isLoading && comments.length === 0 ? (
        <div className="flex justify-center py-6">
          <svg className="animate-spin h-6 w-6 text-amber-500" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
          </svg>
        </div>
            ) : comments.length === 0 ? (
        <div className="text-center py-8 text-slate-400 text-sm">
          {t('comments.noComments')}
        </div>
      ) : (
        <div className="space-y-5">
          {comments.map((comment) => (
            <div key={comment.id} className="flex gap-3 group">
              <img src={(comment.userProfileImageUrl && !comment.userProfileImageUrl.includes('default-profile')) ? comment.userProfileImageUrl : '/default-avatar.svg'} alt="User Profile" className="w-8 h-8 rounded-full object-cover border border-slate-200 bg-white flex-shrink-0" />
              <div className="flex-grow">
                {editingCommentId === comment.id ? (
                  <div className="bg-slate-50 rounded-2xl px-4 py-3 border border-amber-300">
                    <textarea
                      value={editContent}
                      onChange={e => setEditContent(e.target.value)}
                      className="w-full bg-white border border-slate-200 rounded-xl px-3 py-2 text-sm focus:border-amber-400 outline-none mb-2"
                      rows="2"
                    ></textarea>
                    <div className="flex gap-2 justify-end">
                      <button onClick={() => setEditingCommentId(null)} className="text-xs px-3 py-1.5 text-slate-500 hover:bg-slate-200 rounded-lg font-medium transition-colors">{t('comments.cancel')}</button>
                      <button onClick={() => handleUpdate(comment.id)} className="text-xs px-4 py-1.5 bg-amber-500 text-white rounded-lg font-bold transition-colors hover:bg-amber-600">{t('comments.save')}</button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="bg-slate-50 rounded-2xl rounded-tl-none px-4 py-3 text-sm text-slate-800 relative">
                      <div className="flex justify-between items-start mb-0.5">
                        <span className="font-bold block">{comment.username}</span>
                        {currentUser && currentUser.id === comment.userId && (
                          <div className="flex gap-2">
                            <button onClick={() => startEdit(comment)} className="text-[10px] font-semibold text-slate-400 hover:text-amber-600">{t('comments.edit')}</button>
                            <button onClick={() => handleDelete(comment.id)} className="text-[10px] font-semibold text-slate-400 hover:text-red-500">{t('comments.delete')}</button>
                          </div>
                        )}
                      </div>
                      <p className="whitespace-pre-wrap">
                        {comment.showTranslation ? comment.translatedContent : comment.content}
                      </p>
                    </div>
                    <div className="flex items-center gap-4 mt-1.5 px-2 text-xs text-slate-500 font-medium flex-wrap">
                      <span>
                        {new Date(comment.createdAt).toLocaleString(undefined, { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                        {comment.updatedAt && comment.updatedAt !== comment.createdAt && (
                          <span className="italic ml-1">{t('comments.edited')}</span>
                        )}
                      </span>
                      <button 
                        onClick={() => handleLike(comment)} 
                        className={`transition-colors flex items-center gap-1 ${comment.likedByMe ? 'text-red-500 hover:text-red-600' : 'text-slate-400 hover:text-red-500'}`}
                      >
                        <svg className="w-4 h-4" fill={comment.likedByMe ? 'currentColor' : 'none'} stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
                        {comment.likeCount > 0 && <span>{comment.likeCount}</span>}
                      </button>
                      {(() => {
                        const hasKorean = isKoreanText(comment.content);
                        const currentLang = i18n.language;
                        const isDifferentLanguage = (hasKorean && currentLang !== 'ko') || (!hasKorean && currentLang === 'ko');

                        if (!isDifferentLanguage) return null;

                        return (
                          <button 
                            onClick={() => handleTranslate(comment.id)}
                            disabled={comment.isTranslating}
                            className="text-[10px] font-bold text-amber-600 hover:text-amber-700 bg-amber-50 px-2 py-0.5 rounded transition-colors disabled:opacity-50 inline-flex items-center gap-0.5"
                          >
                            <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M3 5h12M9 3v2m1.048 9.5A18.022 18.022 0 016.412 9m6.088 9h7M11 21l5-10 5 10M12.751 5C11.783 10.77 8.07 15.61 3 18.129" />
                            </svg>
                            {comment.isTranslating ? t('common.loading') : comment.showTranslation ? t('comments.showOriginal') : t('comments.translate')}
                          </button>
                        );
                      })()}
                    </div>
                  </>
                )}
              </div>
            </div>
          ))}
          
          {hasNext && (
            <button 
              onClick={() => loadComments(false)}
              className="w-full py-2 text-sm font-semibold text-amber-600 hover:text-amber-700 hover:bg-amber-50 rounded-lg transition-colors mt-4"
            >
              {t('comments.loadMore')}
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default CommentSection;
