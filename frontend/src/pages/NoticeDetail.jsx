import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';

function NoticeDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [notice, setNotice] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

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
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchNotice();
  }, [id]);

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
          <h2 className="text-xl font-bold mb-2">Error Loading Notice</h2>
          <p className="text-red-500 mb-6">{error}</p>
          <button 
             onClick={() => navigate('/notices')}
             className="bg-white text-slate-700 hover:text-amber-600 font-semibold py-2 px-6 rounded-full border border-slate-200 hover:border-amber-400 transition-colors"
          >
            ← Back to Notices
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10 animate-fade-in-up">
      <Link to="/notices" className="inline-flex items-center text-sm font-medium text-slate-500 hover:text-amber-600 transition-colors mb-8">
        <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path></svg>
        Back to Notices
      </Link>

      <article className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden">
        <header className="bg-slate-50 border-b border-slate-100 px-8 py-8">
          <div className="flex items-center gap-3 mb-4">
            {notice.isPinned && (
              <span className="bg-amber-100 text-amber-700 text-xs font-bold px-2 py-1 rounded-md">PINNED</span>
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
      </article>
    </div>
  );
}

export default NoticeDetail;
