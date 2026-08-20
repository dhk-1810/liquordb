import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { fetchAuthToken } from '../utils/auth';

function Admin() {
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
  const [cocktails, setCocktails] = useState([]);

  // 칵테일 데이터 수집 (Seeding API)
  const handleSeedCocktails = async () => {
    setLoading(true);
    setMessage(null);
    setError(null);
    try {
      const jwtData = await fetchAuthToken();
      const headers = {};
      if (jwtData?.accessToken) {
        headers['Authorization'] = `Bearer ${jwtData.accessToken}`;
      }

      const res = await fetch('/api/admin/liquors/seed/cocktails', {
        method: 'POST',
        headers,
      });
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: 데이터 수집 요청 실패`);
      }
      const data = await res.text();
      setMessage(data || '칵테일 정보 수집 요청이 시작되었습니다.');
    } catch (err) {
      console.error('Seed Cocktails Error:', err);
      setError(err.message || '칵테일 데이터 수집 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // DB에 저장된 칵테일 정보 조회 API
  const handleFetchCocktails = async () => {
    setLoading(true);
    setMessage(null);
    setError(null);
    try {
      const res = await fetch('/api/liquors?category=COCKTAIL&pageSize=20');
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: 칵테일 조회 실패`);
      }
      const data = await res.json();
      setCocktails(data.items || data.content || []);
      setMessage('칵테일 정보를 성공적으로 불러왔습니다.');
    } catch (err) {
      console.error('Fetch Cocktails Error:', err);
      setError(err.message || '칵테일 정보 조회 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fade-in">
      <div className="mb-8">
        <h1 className="text-3xl font-extrabold text-slate-900 flex items-center gap-3">
          <span className="text-amber-500">🛡️</span> {t('admin.title', '관리자 대시보드')}
        </h1>
        <p className="text-slate-500 mt-2">
          {t('admin.subtitle', '시스템 관리 및 데이터 동기화 기능을 수행할 수 있습니다.')}
        </p>
      </div>

      {/* Alert Messages */}
      {message && (
        <div className="mb-6 p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-xl text-sm font-medium flex items-center justify-between">
          <span>{message}</span>
          <button onClick={() => setMessage(null)} className="text-emerald-500 hover:text-emerald-700">✕</button>
        </div>
      )}
      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 text-red-800 rounded-xl text-sm font-medium flex items-center justify-between">
          <span>{error}</span>
          <button onClick={() => setError(null)} className="text-red-500 hover:text-red-700">✕</button>
        </div>
      )}

      {/* Main Admin Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Cocktail Management Card */}
        <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
          <div className="w-12 h-12 rounded-xl bg-amber-100 text-amber-600 flex items-center justify-center text-2xl font-bold mb-4">
            🍸
          </div>
          <h2 className="text-xl font-bold text-slate-900 mb-2">
            {t('admin.cocktails', '칵테일 정보 관리')}
          </h2>
          <p className="text-sm text-slate-500 mb-6 leading-relaxed">
            외부 API(TheCocktailDB)에서 칵테일 정보를 수집하거나, DB에 등록된 칵테일목록을 조회합니다.
          </p>

          <div className="flex flex-col gap-3">
            <button
              onClick={handleFetchCocktails}
              disabled={loading}
              className="w-full bg-amber-500 hover:bg-amber-600 active:bg-amber-700 text-white py-2.5 px-4 rounded-xl font-semibold transition-all shadow-sm shadow-amber-500/20 disabled:opacity-50 flex items-center justify-center gap-2"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <>
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                  <span>{t('admin.fetchCocktails', '칵테일 정보 조회')}</span>
                </>
              )}
            </button>

            <button
              onClick={handleSeedCocktails}
              disabled={loading}
              className="w-full border border-slate-300 hover:bg-slate-50 text-slate-700 py-2.5 px-4 rounded-xl font-semibold transition-all text-sm disabled:opacity-50 flex items-center justify-center gap-2"
            >
              <svg className="w-4 h-4 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              <span>{t('admin.seedCocktails', '외부 칵테일 데이터 수집 (시딩)')}</span>
            </button>
          </div>
        </div>

        {/* Notice Management Card */}
        <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
          <div>
            <div className="w-12 h-12 rounded-xl bg-amber-100 text-amber-600 flex items-center justify-center text-2xl font-bold mb-4">
              📢
            </div>
            <h2 className="text-xl font-bold text-slate-900 mb-2">
              {t('admin.notices', '공지사항 관리')}
            </h2>
            <p className="text-sm text-slate-500 mb-6 leading-relaxed">
              {t('admin.noticesDesc', '서비스 공지사항을 등록, 수정 및 삭제합니다.')}
            </p>
          </div>

          <div className="flex flex-col gap-3">
            <Link
              to="/notices"
              className="w-full bg-amber-500 hover:bg-amber-600 active:bg-amber-700 text-white py-2.5 px-4 rounded-xl font-semibold transition-all shadow-sm shadow-amber-500/20 flex items-center justify-center gap-2"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              <span>{t('admin.viewNotices', '공지사항 목록 이동')}</span>
            </Link>
          </div>
        </div>
      </div>

      {/* Cocktails List Result Section */}
      {cocktails.length > 0 && (
        <div className="mt-10 bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-bold text-slate-900">
              조회된 칵테일 목록 ({cocktails.length}건)
            </h3>
            <button
              onClick={() => setCocktails([])}
              className="text-sm text-slate-400 hover:text-slate-600"
            >
              닫기
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {cocktails.map((item) => (
              <div key={item.id} className="border border-slate-100 rounded-xl p-3 flex items-center gap-3 bg-slate-50 hover:bg-amber-50/50 transition-colors">
                <img
                  src={item.imageUrl || '/placeholder.png'}
                  alt={item.name}
                  className="w-12 h-12 rounded-lg object-cover bg-slate-200"
                  onError={(e) => { e.target.src = '/placeholder.png'; }}
                />
                <div className="overflow-hidden">
                  <p className="font-semibold text-slate-800 truncate text-sm">{item.name}</p>
                  <p className="text-xs text-slate-400 truncate">{item.origin || item.brewery || 'COCKTAIL'}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default Admin;
