import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchAuthToken } from '../utils/auth';

function CommentSection({ reviewId, initialCommentCount, currentUser }) {
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
        window.alert('You must be logged in to comment.');
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
      setCommentCount(prev => prev + 1);
      
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
      window.alert('Error posting comment.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLike = async (commentId, isCurrentlyLiked) => {
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        navigate('/signin');
        return;
      }
      
      // We don't have isCurrentlyLiked from the backend API directly unless it was added, 
      // but assuming the user just toggles or clicks "like"
      const response = await fetch(`/api/comments/${commentId}/like`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });
      
      if (response.ok) {
        setComments(prev => prev.map(c => {
          if (c.id === commentId) {
            return { ...c, likeCount: c.likeCount + 1 };
          }
          return c;
        }));
      } else if (response.status === 409) {
        // Already liked, so cancel like
        const cancelRes = await fetch(`/api/comments/${commentId}/cancel-like`, {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${jwtData.accessToken}`
          }
        });
        if (cancelRes.ok) {
          setComments(prev => prev.map(c => {
            if (c.id === commentId) {
              return { ...c, likeCount: Math.max(0, c.likeCount - 1) };
            }
            return c;
          }));
        }
      } else {
        console.error("Failed to like comment:", response.status);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (commentId) => {
    if (!window.confirm("Are you sure you want to delete this comment?")) return;
    try {
      const jwtData = await fetchAuthToken();
      const response = await fetch(`/api/comments/${commentId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${jwtData.accessToken}` }
      });
      if (response.ok) {
        setComments(prev => prev.filter(c => c.id !== commentId));
        setCommentCount(prev => Math.max(0, prev - 1));
      } else {
        throw new Error("Failed to delete comment");
      }
    } catch (err) {
      console.error(err);
      window.alert("Error deleting comment");
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
      window.alert("Error updating comment");
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
          Comments 
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
          <option value="COMMENT_ID-DESC">Latest</option>
          <option value="COMMENT_ID-ASC">Oldest</option>
          <option value="LIKE_COUNT-DESC">Most Liked</option>
        </select>
      </div>

      {/* Comment Input */}
      <form onSubmit={handleSubmit} className="mb-8 flex gap-3">
        <img src="/default-avatar.svg" alt="User Profile" className="w-8 h-8 rounded-full object-cover border border-slate-200 bg-white flex-shrink-0" />
        <div className="flex-grow">
          <input 
            type="text" 
            placeholder="Write a comment..." 
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            onClick={() => {
              if (!currentUser) {
                navigate('/signin');
              }
            }}
            onFocus={() => {
              if (!currentUser) {
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
                {isSubmitting ? 'Posting...' : 'Post'}
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
          No comments yet. Be the first to share your thoughts!
        </div>
      ) : (
        <div className="space-y-5">
          {comments.map((comment) => (
            <div key={comment.id} className="flex gap-3 group">
              <img src="/default-avatar.svg" alt="User Profile" className="w-8 h-8 rounded-full object-cover border border-slate-200 bg-white flex-shrink-0" />
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
                      <button onClick={() => setEditingCommentId(null)} className="text-xs px-3 py-1.5 text-slate-500 hover:bg-slate-200 rounded-lg font-medium transition-colors">Cancel</button>
                      <button onClick={() => handleUpdate(comment.id)} className="text-xs px-4 py-1.5 bg-amber-500 text-white rounded-lg font-bold transition-colors hover:bg-amber-600">Save</button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="bg-slate-50 rounded-2xl rounded-tl-none px-4 py-3 text-sm text-slate-800 relative">
                      <div className="flex justify-between items-start mb-0.5">
                        <span className="font-bold block">{comment.username}</span>
                        {currentUser && currentUser.id === comment.userId && (
                          <div className="flex gap-2">
                            <button onClick={() => startEdit(comment)} className="text-[10px] font-semibold text-slate-400 hover:text-amber-600">Edit</button>
                            <button onClick={() => handleDelete(comment.id)} className="text-[10px] font-semibold text-slate-400 hover:text-red-500">Delete</button>
                          </div>
                        )}
                      </div>
                      <p className="whitespace-pre-wrap">{comment.content}</p>
                    </div>
                    <div className="flex items-center gap-4 mt-1.5 px-2 text-xs text-slate-500 font-medium">
                      <span>
                        {new Date(comment.createdAt).toLocaleDateString()}
                        {comment.updatedAt && comment.updatedAt !== comment.createdAt && (
                          <span className="italic ml-1">(edited)</span>
                        )}
                      </span>
                      <button 
                        onClick={() => handleLike(comment.id)} 
                        className="text-slate-400 hover:text-red-500 transition-colors flex items-center gap-1"
                      >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
                        {comment.likeCount > 0 && <span>{comment.likeCount}</span>}
                      </button>
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
              Load more comments
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default CommentSection;
