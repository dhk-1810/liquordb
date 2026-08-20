import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { fetchAuthToken } from '../utils/auth';

function NoticesList() {
  const { t } = useTranslation();
  const [notices, setNotices] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [isWriteModalOpen, setIsWriteModalOpen] = useState(false);
  const [writeTitle, setWriteTitle] = useState('');
  const [writeContent, setWriteContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState(null);

  const fetchNotices = async (pageNumber = 0) => {
    try {
      setIsLoading(true);
      setError(null);

      const response = await fetch(`/api/notices?page=${pageNumber}&limit=10`);
      if (!response.ok) {
        throw new Error(t('notices.fetchError'));
      }

      const data = await response.json();

      setNotices(data.content || []);
      setPage(data.page);
      setHasNext(data.hasNext);
      
      const totalElems = data.totalElements || 0;
      const pageSize = data.size || 10;
      setTotalPages(Math.ceil(totalElems / pageSize));
    } catch (err) {
      console.error(err);
      setError(t('notices.fetchError'));
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchNotices(0);

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
  }, []);

  const handleCreateNotice = async (e) => {
    e.preventDefault();
    if (!writeTitle.trim() || !writeContent.trim()) {
      alert('제목과 내용을 모두 입력해 주세요.');
      return;
    }
    try {
      setIsSubmitting(true);
      const authData = await fetchAuthToken();
      const headers = {
        'Content-Type': 'application/json',
      };
      if (authData?.accessToken) {
        headers['Authorization'] = `Bearer ${authData.accessToken}`;
      }

      const res = await fetch('/api/admin/notices', {
        method: 'POST',
        headers,
        body: JSON.stringify({
          title: writeTitle,
          content: writeContent,
        }),
      });

      if (!res.ok) {
        throw new Error('Failed to create notice');
      }

      setWriteTitle('');
      setWriteContent('');
      setIsWriteModalOpen(false);
      setSuccessMessage(t('notices.createSuccess', '공지사항이 등록되었습니다.'));
      setTimeout(() => setSuccessMessage(null), 3000);
      fetchNotices(0);
    } catch (err) {
      console.error(err);
      alert('공지사항 등록 중 오류가 발생했습니다.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      fetchNotices(newPage);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      <div className="mb-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">{t('notices.title')}</h1>
          <p className="text-lg text-slate-600">{t('notices.subtitle')}</p>
        </div>
        {isAdmin && (
          <button
            onClick={() => setIsWriteModalOpen(true)}
            className="inline-flex items-center gap-2 bg-amber-500 hover:bg-amber-600 active:bg-amber-700 text-white font-semibold px-4 py-2.5 rounded-xl shadow-sm shadow-amber-500/20 transition-all text-sm self-start sm:self-auto"
          >
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            <span>{t('notices.writeNotice', '공지 작성')}</span>
          </button>
        )}
      </div>

      {successMessage && (
        <div className="mb-6 bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-sm font-medium">
          {successMessage}
        </div>
      )}

      {error && (
        <div className="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium">
          {error}
        </div>
      )}

      {/* List */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        {notices.length === 0 && !isLoading ? (
          <div className="text-center py-20">
            <h3 className="text-lg font-semibold text-slate-600">{t('notices.noNotices')}</h3>
          </div>
        ) : (
          <ul className="divide-y divide-slate-100">
            {notices.map((notice) => (
              <li key={notice.id} className="hover:bg-slate-50 transition-colors">
                <Link to={`/notices/${notice.id}`} className="block px-6 py-5">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      {notice.isPinned && (
                        <span className="bg-amber-100 text-amber-700 text-xs font-bold px-2 py-1 rounded-md">{t('notices.pinned')}</span>
                      )}
                      <h3 className="text-lg font-semibold text-slate-800 line-clamp-1">{notice.title}</h3>
                    </div>
                    <span className="text-sm font-medium text-slate-400 whitespace-nowrap ml-4">
                      {new Date(notice.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* Loading state / Load more */}
      <div className="mt-8 text-center">
        {isLoading ? (
          <div className="inline-flex items-center justify-center">
            <svg className="animate-spin h-8 w-8 text-amber-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
          </div>
        ) : (
          <div className="flex items-center justify-center gap-2 mt-4">
            <button
              onClick={() => handlePageChange(page - 1)}
              disabled={page === 0}
              className={`px-4 py-2 rounded-lg font-bold text-sm transition-all ${
                page === 0
                  ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                  : 'bg-white border-2 border-slate-200 text-slate-600 hover:border-amber-500 hover:text-amber-600'
              }`}
            >
              {t('notices.previous')}
            </button>
            
            <span className="px-4 py-2 text-sm font-medium text-slate-600">
              {t('notices.page', { current: page + 1, total: totalPages || 1 })}
            </span>

            <button
              onClick={() => handlePageChange(page + 1)}
              disabled={!hasNext}
              className={`px-4 py-2 rounded-lg font-bold text-sm transition-all ${
                !hasNext
                  ? 'bg-slate-100 text-slate-400 cursor-not-allowed'
                  : 'bg-white border-2 border-slate-200 text-slate-600 hover:border-amber-500 hover:text-amber-600'
              }`}
            >
              {t('notices.next')}
            </button>
          </div>
        )}
      </div>

      {/* Write Notice Modal */}
      {isWriteModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm animate-fade-in">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-2xl border border-slate-100 animate-scale-up">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-slate-900 flex items-center gap-2">
                <span>📢</span> {t('notices.writeNotice', '공지 작성')}
              </h3>
              <button
                onClick={() => setIsWriteModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 transition-colors"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreateNotice} className="space-y-4">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1.5">
                  {t('notices.titleLabel', '제목')}
                </label>
                <input
                  type="text"
                  value={writeTitle}
                  onChange={(e) => setWriteTitle(e.target.value)}
                  placeholder={t('notices.titlePlaceholder', '제목을 입력하세요')}
                  required
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all text-sm font-medium"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-1.5">
                  {t('notices.contentLabel', '내용')}
                </label>
                <textarea
                  value={writeContent}
                  onChange={(e) => setWriteContent(e.target.value)}
                  placeholder={t('notices.contentPlaceholder', '공지 내용을 입력하세요...')}
                  rows={6}
                  required
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all text-sm resize-y"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsWriteModalOpen(false)}
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
          </div>
        </div>
      )}
    </div>
  );
}

export default NoticesList;
