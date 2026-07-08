import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function SignIn() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });
      
      const data = res.ok ? await res.json().catch(() => ({})) : await res.json().catch(() => ({}));
      
      if (!res.ok) {
        if (data.details && Object.keys(data.details).length > 0) {
          const firstError = Object.values(data.details)[0]?.message;
          setError(firstError || data.message || t('auth.signIn.invalidCredentials'));
        } else {
          setError(data.message || t('auth.signIn.invalidCredentials'));
        }
        return;
      }

      if (data.userDto && data.userDto.status === 'WITHDRAWN') {
        const confirmRestore = window.confirm(
          '탈퇴 신청된 계정입니다. 1주일 이내이므로 계정을 복구하고 다시 이용하실 수 있습니다. 계정을 복구하고 로그인하시겠습니까?'
        );
        if (confirmRestore) {
          try {
            const restoreRes = await fetch(`/api/auth/restore?email=${encodeURIComponent(data.userDto.email)}`, {
              method: 'POST',
              headers: {
                'Authorization': `Bearer ${data.accessToken}`
              }
            });
            if (restoreRes.ok) {
              window.alert('계정이 성공적으로 복구되었습니다!');
              localStorage.setItem('isLoggedIn', 'true');
              navigate('/');
            } else {
              const err = await restoreRes.json().catch(() => ({}));
              throw new Error(err.message || 'Failed to restore account');
            }
          } catch (err) {
            window.alert(`복구 실패: ${err.message}`);
          }
        } else {
          return;
        }
      } else {
        localStorage.setItem('isLoggedIn', 'true');
        navigate('/');
      }
    } catch (err) {
      console.error(err);
      setError(t('auth.signIn.networkError'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 py-12 animate-fade-in-up">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden relative">
        {/* Decorative background element */}
        <div className="absolute top-0 left-0 -ml-16 -mt-16 w-40 h-40 bg-amber-400/20 rounded-full blur-3xl pointer-events-none"></div>
        <div className="absolute bottom-0 right-0 -mr-16 -mb-16 w-32 h-32 bg-orange-400/20 rounded-full blur-3xl pointer-events-none"></div>

        <div className="p-8 relative z-10">
          <div className="text-center mb-10">
            <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">{t('auth.signIn.title')}</h2>
            <p className="text-sm text-slate-500 mt-2">{t('auth.signIn.subtitle')}</p>
          </div>

          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium flex items-start gap-2">
              <svg className="w-5 h-5 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-1.5" htmlFor="email">
                {t('auth.signIn.email')}
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all duration-200"
                placeholder="you@example.com"
              />
            </div>
            
            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-sm font-semibold text-slate-700" htmlFor="password">
                  {t('auth.signIn.password')}
                </label>
                <Link to="/find-password" className="text-xs font-semibold text-amber-600 hover:text-amber-700 transition-colors">
                  {t('auth.signIn.forgotPassword')}
                </Link>
              </div>
              <input
                id="password"
                name="password"
                type="password"
                required
                value={formData.password}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-xl bg-slate-50 border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all duration-200"
                placeholder="••••••••"
              />
            </div>

            <div className="pt-2">
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-amber-500 hover:bg-amber-600 text-white font-bold py-3.5 px-4 rounded-xl transition-colors duration-200 shadow-sm shadow-amber-500/30 flex items-center justify-center disabled:opacity-70 disabled:cursor-not-allowed"
              >
                {loading ? (
                  <svg className="animate-spin h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                ) : (
                  t('auth.signIn.submit')
                )}
              </button>
            </div>
          </form>

          <div className="mt-8 text-center">
            <p className="text-sm font-medium text-slate-600">
              {t('auth.signIn.noAccount')}{' '}
              <Link to="/signup" className="text-amber-600 hover:text-amber-700 hover:underline transition-all">
                {t('auth.signIn.createAccount')}
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SignIn;
