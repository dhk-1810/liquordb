import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function NoticesList() {
  const { t } = useTranslation();
  const [notices, setNotices] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

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
  }, []);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      fetchNotices(newPage);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      <div className="mb-8">
        <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight mb-2">{t('notices.title')}</h1>
        <p className="text-lg text-slate-600">{t('notices.subtitle')}</p>
      </div>

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
    </div>
  );
}

export default NoticesList;
