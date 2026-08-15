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

  const [replyingToComment, setReplyingToComment] = useState(null);
  const [replyContent, setReplyContent] = useState('');

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

  const handleSubmit = async (e, parentId = null) => {
    if (e) e.preventDefault();
    const content = parentId ? replyContent : newComment;
    if (!content.trim()) return;
    
    setIsSubmitting(true);
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert(t('comments.loginToComment'));
        navigate('/signin');
        return;
      }

      const bodyData = { content };
      if (parentId) {
        bodyData.parentId = parentId;
      }

      const response = await fetch(`/api/reviews/${reviewId}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${jwtData.accessToken}`
        },
        body: JSON.stringify(bodyData)
      });

      if (!response.ok) throw new Error('Failed to post comment');
      
      if (parentId) {
        setReplyContent('');
        setReplyingToComment(null);
      } else {
        setNewComment('');
      }
      setCommentCount(prev => {
        const next = prev + 1;
        if (onCommentCountChange) onCommentCountChange(next);
        return next;
      });
      
      if (sortBy === 'COMMENT_ID' && sortDirection === 'DESC') {
        loadComments(true);
      } else {
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
        setOpenReplies(prev => {
          const next = { ...prev };
          Object.keys(next).forEach(parentId => {
            if (next[parentId]?.list) {
              next[parentId] = {
                ...next[parentId],
                list: next[parentId].list.map(reply => 
                  reply.id === comment.id 
                    ? { ...reply, likedByMe: nextLikedByMe, likeCount: nextLikeCount }
                    : reply
                )
              };
            }
          });
          return next;
        });
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

  const [openReplies, setOpenReplies] = useState({}); // { [commentId]: { loading: boolean, list: [] } }

  const handleToggleReplies = async (commentId) => {
    if (openReplies[commentId]?.list) {
      // Toggle visibility by removing or marking closed
      setOpenReplies(prev => {
        const next = { ...prev };
        delete next[commentId];
        return next;
      });
      return;
    }

    setOpenReplies(prev => ({ ...prev, [commentId]: { loading: true, list: [] } }));
    try {
      const response = await fetch(`/api/comments/${commentId}/replies`);
      if (!response.ok) throw new Error('Failed to load replies');
      const data = await response.json();
      setOpenReplies(prev => ({
        ...prev,
        [commentId]: { loading: false, list: data }
      }));
    } catch (err) {
      console.error(err);
      setOpenReplies(prev => {
        const next = { ...prev };
        delete next[commentId];
        return next;
      });
    }
  };

  const renderCommentNode = (comment, level = 0) => {
    const isReplyingThis = replyingToComment?.id === comment.id;
    const replyState = openReplies[comment.id];
    const childReplies = replyState?.list || [];

    return (
      <div key={comment.id} className="relative group">
        <div className="flex gap-3 relative items-start">
          {/* Curved connecting line from parent profile avatar */}
          {level > 0 && (
            <div 
              className="absolute -left-6 -top-8 w-6 h-12 border-l-2 border-b-2 border-slate-200/80 rounded-bl-xl pointer-events-none"
              style={{ zIndex: 0 }}
            />
          )}

          <img 
            src={(comment.userProfileImageUrl && !comment.userProfileImageUrl.includes('default-profile')) ? comment.userProfileImageUrl : '/default-avatar.svg'} 
            alt="User Profile" 
            className="w-8 h-8 rounded-full object-cover border border-slate-200 bg-white flex-shrink-0 z-10 relative" 
          />

          <div className="flex-grow min-w-0">
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
                  
                  <button
                    onClick={() => {
                      if (!currentUser) {
                        window.alert(t('comments.loginToComment'));
                        navigate('/signin');
                        return;
                      }
                      if (replyingToComment?.id === comment.id) {
                        setReplyingToComment(null);
                        setReplyContent('');
                      } else {
                        setReplyingToComment(comment);
                        setReplyContent('');
                      }
                    }}
                    className="text-slate-400 hover:text-amber-600 transition-colors text-xs font-semibold"
                  >
                    {t('comments.reply')}
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

        {/* Inline Reply Form */}
        {isReplyingThis && (
          <form onSubmit={(e) => handleSubmit(e, comment.id)} className="ml-11 mt-3 mb-4 flex gap-2">
            <img 
              src={(currentUser && currentUser.profileImageUrl && !currentUser.profileImageUrl.includes('default-profile')) ? currentUser.profileImageUrl : '/default-avatar.svg'} 
              alt="User Profile" 
              className="w-7 h-7 rounded-full object-cover border border-slate-200 bg-white flex-shrink-0" 
            />
            <div className="flex-grow">
              <div className="text-[11px] font-semibold text-amber-600 mb-1">
                {t('comments.replyTo', { username: comment.username })}
              </div>
              <input 
                type="text" 
                placeholder={t('comments.placeholder')} 
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                autoFocus
                className="w-full bg-slate-50 border border-amber-200 rounded-xl px-3 py-2 text-sm focus:bg-white focus:border-amber-400 outline-none transition-all"
              />
              <div className="mt-1.5 flex gap-2 justify-end">
                <button 
                  type="button"
                  onClick={() => { setReplyingToComment(null); setReplyContent(''); }}
                  className="text-xs px-2.5 py-1 text-slate-400 hover:text-slate-600 transition-colors"
                >
                  {t('comments.cancel')}
                </button>
                <button 
                  type="submit"
                  disabled={isSubmitting || !replyContent.trim()}
                  className="bg-amber-500 hover:bg-amber-600 text-white text-xs font-bold px-3 py-1 rounded-lg transition-colors disabled:opacity-50"
                >
                  {isSubmitting ? t('comments.posting') : t('comments.post')}
                </button>
              </div>
            </div>
          </form>
        )}

        {/* Toggle View Replies Button (Only for top-level comments) */}
        {level === 0 && (
          <div className="ml-11 mt-2">
            {replyState?.loading ? (
              <div className="text-xs text-amber-600 font-semibold animate-pulse flex items-center gap-1">
                <svg className="animate-spin h-3 w-3 text-amber-500" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                {t('common.loading', '불러오는 중...')}
              </div>
            ) : replyState?.list ? (
              <button
                onClick={() => handleToggleReplies(comment.id)}
                className="text-xs font-bold text-amber-600 hover:text-amber-700 transition-colors flex items-center gap-1.5 mb-2"
              >
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 15l7-7 7 7" />
                </svg>
                {t('comments.hideReplies', '답글 숨기기')}
              </button>
            ) : (
              <button
                onClick={() => handleToggleReplies(comment.id)}
                className="text-xs font-bold text-amber-600 hover:text-amber-700 transition-colors flex items-center gap-1.5"
              >
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
                </svg>
                {t('comments.replyCount', '답글 보기')}
              </button>
            )}
          </div>
        )}

        {/* Nested Child Comments (Only rendered when opened) */}
        {childReplies.length > 0 && (
          <div className="ml-10 space-y-5 mt-3">
            {childReplies.map(child => renderCommentNode(child, level + 1))}
          </div>
        )}
      </div>
    );
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

      {/* Main Comment Input */}
      <form onSubmit={(e) => handleSubmit(e, null)} className="mb-8 flex gap-3">
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
        <div className="space-y-6">
          {comments.filter(c => !c.parentId).map(comment => renderCommentNode(comment, 0))}
          
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
