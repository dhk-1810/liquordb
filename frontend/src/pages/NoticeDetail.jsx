import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { fetchAuthToken } from '../utils/auth';

function NoticeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [notice, setNotice] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);

  // Edit mode state
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState('');
  const [editContent, setEditContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [actionMessage, setActionMessage] = useState(null);
  const [actionError, setActionError] = useState(null);

  useEffect(() => {
    const fetchNotice = async () => {
      try {
        setIsLoading(true);
        const response = await fetch(`/api/notices/${id}`);
        
        if (!response.ok) {
          if (response.status === 404) {
             throw new Error('Notice not found.');
          }
          throw new Error('Failed to fetch notice details.');
        }

        const data = await response.json();
        setNotice(data);
        setEditTitle(data.title || '');
        setEditContent(data.content || '');
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchNotice();

    const checkUserRole = async () => {
      try {
        const authData = await fetchAuthToken();
        if (authData?.userDto?.role === 'ADMIN') {
          setIsAdmin(true);
        }
      } catch (err) {
        console.error('Failed to check admin status', err);
      }
    };
    checkUserRole();
  }, [id]);

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!editTitle.trim() || !editContent.trim()) {
      alert('제목과 내용을 모두 입력해 주세요.');
      return;
    }

    try {
      setIsSubmitting(true);
      setActionError(null);
      const authData = await fetchAuthToken();
      const headers = {
        'Content-Type': 'application/json',
      };
      if (authData?.accessToken) {
        headers['Authorization'] = `Bearer ${authData.accessToken}`;
      }

      const res = await fetch(`/api/admin/notices/${id}`, {
        method: 'PATCH',
        headers,
        body: JSON.stringify({
          title: editTitle,
          content: editContent,
        }),
      });

      if (!res.ok) {
        throw new Error('공지사항 수정에 실패했습니다.');
      }

      const updated = await res.json();
      setNotice(updated);
      setIsEditing(false);
      setActionMessage(t('notices.updateSuccess', '공지사항이 수정되었습니다.'));
      setTimeout(() => setActionMessage(null), 3000);
    } catch (err) {
      console.error(err);
      setActionError(err.message || '공지사항 수정 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleTogglePin = async () => {
    try {
      setIsSubmitting(true);
      setActionError(null);
      const authData = await fetchAuthToken();
      const headers = {};
      if (authData?.accessToken) {
        headers['Authorization'] = `Bearer ${authData.accessToken}`;
      }

      const res = await fetch(`/api/admin/notices/${id}/pin`, {
        method: 'PATCH',
        headers,
      });

      if (!res.ok) {
        throw new Error('고정 상태 변경에 실패했습니다.');
      }

      const updated = await res.json();
      setNotice(updated);
      setActionMessage(t('notices.pinSuccess', '상단 고정 상태가 변경되었습니다.'));
      setTimeout(() => setActionMessage(null), 3000);
    } catch (err) {
      console.error(err);
      setActionError(err.message || '상단 고정 변경 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm(t('notices.confirmDelete', '정말로 이 공지사항을 삭제하시겠습니까?'))) {
      return;
    }

    try {
      setIsSubmitting(true);
      setActionError(null);
      const authData = await fetchAuthToken();
      const headers = {};
      if (authData?.accessToken) {
        headers['Authorization'] = `Bearer ${authData.accessToken}`;
      }

      const res = await fetch(`/api/admin/notices/${id}`, {
        method: 'DELETE',
        headers,
      });

      if (!res.ok) {
        throw new Error('공지사항 삭제에 실패했습니다.');
      }

      navigate('/notices');
    } catch (err) {
      console.error(err);
      setActionError(err.message || '공지사항 삭제 중 오류가 발생했습니다.');
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-20 text-center animate-fade-in-up">
        <svg className="animate-spin inline-block h-8 w-8 text-amber-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-20 text-center animate-fade-in-up">
        <div className="bg-red-50 border border-red-200 text-red-600 px-6 py-8 rounded-2xl shadow-sm">
          <svg className="w-12 h-12 mx-auto mb-4 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
             <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <h2 className="text-xl font-bold mb-2">{t('notices.errorLoading')}</h2>
          <p className="text-red-500 mb-6">{error}</p>
          <button 
             onClick={() => navigate('/notices')}
             className="bg-white text-slate-700 hover:text-amber-600 font-semibold py-2 px-6 rounded-full border border-slate-200 hover:border-amber-400 transition-colors"
          >
            ← {t('notices.backToNotices')}
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      <div className="flex items-center justify-between mb-8">
        <Link to="/notices" className="inline-flex items-center text-sm font-medium text-slate-500 hover:text-amber-600 transition-colors">
          <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path></svg>
          {t('notices.backToNotices')}
        </Link>

        {isAdmin && !isEditing && (
          <div className="flex items-center gap-2">
            <button
              onClick={handleTogglePin}
              disabled={isSubmitting}
              title={notice?.isPinned ? t('notices.unpinNotice', '고정 해제') : t('notices.pinNotice', '상단 고정')}
              className={`px-3 py-1.5 rounded-xl border text-xs font-semibold flex items-center gap-1.5 transition-all ${
                notice?.isPinned
                  ? 'border-amber-300 bg-amber-50 text-amber-700 hover:bg-amber-100'
                  : 'border-slate-200 bg-white text-slate-600 hover:bg-slate-50'
              }`}
            >
              <span>📌</span>
              <span>{notice?.isPinned ? t('notices.unpinNotice', '고정 해제') : t('notices.pinNotice', '상단 고정')}</span>
            </button>

            <button
              onClick={() => {
                setEditTitle(notice?.title || '');
                setEditContent(notice?.content || '');
                setIsEditing(true);
              }}
              disabled={isSubmitting}
              className="px-3 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 text-xs font-semibold flex items-center gap-1.5 transition-all"
            >
              <svg className="w-3.5 h-3.5 text-slate-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
              <span>{t('common.edit', '수정')}</span>
            </button>

            <button
              onClick={handleDelete}
              disabled={isSubmitting}
              className="px-3 py-1.5 rounded-xl border border-red-200 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-semibold flex items-center gap-1.5 transition-all"
            >
              <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
              <span>{t('common.delete', '삭제')}</span>
            </button>
          </div>
        )}
      </div>

      {actionMessage && (
        <div className="mb-6 bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-sm font-medium">
          {actionMessage}
        </div>
      )}

      {actionError && (
        <div className="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium">
          {actionError}
        </div>
      )}

      <article className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
        {isEditing ? (
          <form onSubmit={handleUpdate} className="p-8 space-y-6">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                {t('notices.titleLabel', '제목')}
              </label>
              <input
                type="text"
                value={editTitle}
                onChange={(e) => setEditTitle(e.target.value)}
                required
                className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 text-base font-semibold"
              />
            </div>

            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-2">
                {t('notices.contentLabel', '내용')}
              </label>
              <textarea
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                rows={10}
                required
                className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 text-sm leading-relaxed resize-y"
              />
            </div>

            <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
              <button
                type="button"
                onClick={() => {
                  setIsEditing(false);
                  setEditTitle(notice?.title || '');
                  setEditContent(notice?.content || '');
                }}
                className="px-4 py-2 rounded-xl text-sm font-semibold text-slate-600 hover:bg-slate-100 transition-colors"
              >
                {t('common.cancel', '취소')}
              </button>
              <button
                type="submit"
                disabled={isSubmitting}
                className="px-5 py-2 rounded-xl text-sm font-semibold bg-amber-500 hover:bg-amber-600 active:bg-amber-700 text-white shadow-sm shadow-amber-500/20 transition-all disabled:opacity-50 flex items-center gap-2"
              >
                {isSubmitting ? (
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                ) : null}
                <span>{t('common.save', '저장')}</span>
              </button>
            </div>
          </form>
        ) : (
          <>
            <header className="bg-slate-50 border-b border-slate-100 px-8 py-8">
              <div className="flex items-center gap-3 mb-4">
                {notice.isPinned && (
                  <span className="bg-amber-100 text-amber-700 text-xs font-bold px-2 py-1 rounded-md">{t('notices.pinned')}</span>
                )}
                <span className="text-sm font-medium text-slate-500">
                  {new Date(notice.createdAt).toLocaleString()}
                </span>
              </div>
              <h1 className="text-3xl font-extrabold text-slate-900 leading-tight mb-4">
                {notice.title}
              </h1>
              <div className="flex items-center gap-2 text-sm text-slate-600">
                <span className="w-6 h-6 rounded-full bg-slate-200 flex items-center justify-center font-bold text-xs">
                   {notice.authorUsername ? notice.authorUsername.charAt(0).toUpperCase() : 'A'}
                </span>
                <span className="font-medium">{notice.authorUsername || 'Admin'}</span>
              </div>
            </header>

            <div className="px-8 py-10">
              <div className="prose prose-slate prose-amber max-w-none whitespace-pre-wrap leading-relaxed text-slate-700">
                {notice.content}
              </div>
            </div>
          </>
        )}
      </article>
    </div>
  );
}

export default NoticeDetail;
