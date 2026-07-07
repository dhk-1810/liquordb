import { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

function ResetPassword() {
  const { token } = useParams();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [formData, setFormData] = useState({ password: '', confirmPassword: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [validationError, setValidationError] = useState('');

  const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/;

  useEffect(() => {
    if (!token) {
      setError(t('auth.resetPassword.invalidAccess'));
    }
  }, [token, t]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setValidationError('');
  };

  const validate = () => {
    if (!PASSWORD_REGEX.test(formData.password)) {
      setValidationError(t('auth.resetPassword.passwordHint'));
      return false;
    }
    if (formData.password !== formData.confirmPassword) {
      setValidationError(t('auth.resetPassword.mismatch'));
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setValidationError('');

    if (!validate()) return;

    setLoading(true);
    try {
      const res = await fetch(`/api/auth/reset-password/${token}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password: formData.password }),
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        if (data.details && Object.keys(data.details).length > 0) {
          const firstError = Object.values(data.details)[0]?.message || Object.values(data.details)[0];
          setError(firstError || data.message || t('auth.resetPassword.resetFailed'));
        } else {
          setError(data.message || t('auth.resetPassword.resetFailed'));
        }
        return;
      }

      setSuccess(true);
      setTimeout(() => navigate('/signin'), 3000);
    } catch (err) {
      console.error(err);
      setError(t('auth.resetPassword.networkError'));
    } finally {
      setLoading(false);
    }
  };

  const getPasswordStrength = (pwd) => {
    if (!pwd) return { level: 0, label: '', color: '' };
    let score = 0;
    if (pwd.length >= 8) score++;
    if (/[A-Z]/.test(pwd)) score++;
    if (/[a-z]/.test(pwd)) score++;
    if (/\d/.test(pwd)) score++;
    if (/[@$!%*?&]/.test(pwd)) score++;

    if (score <= 2) return { level: score, label: t('auth.resetPassword.strength.weak'), color: 'bg-red-400' };
    if (score <= 3) return { level: score, label: t('auth.resetPassword.strength.fair'), color: 'bg-amber-400' };
    if (score === 4) return { level: score, label: t('auth.resetPassword.strength.strong'), color: 'bg-emerald-400' };
    return { level: score, label: t('auth.resetPassword.strength.veryStrong'), color: 'bg-emerald-500' };
  };

  const strength = getPasswordStrength(formData.password);

  const PasswordToggle = ({ show, onToggle }) => (
    <button
      type="button"
      onClick={onToggle}
      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
      tabIndex={-1}
    >
      {show ? (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
        </svg>
      ) : (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
      )}
    </button>
  );

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-4 py-12 animate-fade-in-up">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-xl shadow-slate-200/50 border border-slate-100 overflow-hidden relative">
        <div className="absolute top-0 right-0 -mr-16 -mt-16 w-40 h-40 bg-amber-400/20 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 left-0 -ml-16 -mb-16 w-32 h-32 bg-orange-400/20 rounded-full blur-3xl pointer-events-none" />

        <div className="p-8 relative z-10">
          {/* Icon */}
          <div className="flex justify-center mb-6">
            <div className="w-16 h-16 rounded-2xl bg-amber-50 border border-amber-100 flex items-center justify-center shadow-sm">
              <svg className="w-8 h-8 text-amber-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.8}
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
          </div>

          <div className="text-center mb-8">
            <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">{t('auth.resetPassword.title')}</h2>
            <p className="text-sm text-slate-500 mt-2 leading-relaxed">{t('auth.resetPassword.subtitle')}</p>
          </div>

          {/* Global error */}
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium flex items-start gap-2">
              <svg className="w-5 h-5 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div>
                <span>{error}</span>
                {(error.includes('링크') || error.includes('expired') || error.includes('invalid')) && (
                  <div className="mt-2">
                    <Link to="/find-password" className="text-amber-600 hover:text-amber-700 font-semibold underline underline-offset-2">
                      {t('auth.resetPassword.retryLink')}
                    </Link>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Success */}
          {success ? (
            <div className="flex flex-col items-center gap-5 text-center">
              <div className="w-20 h-20 rounded-full bg-emerald-50 border-2 border-emerald-200 flex items-center justify-center">
                <svg className="w-10 h-10 text-emerald-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <div>
                <p className="text-lg font-bold text-slate-900">{t('auth.resetPassword.successTitle')}</p>
                <p className="text-sm text-slate-500 mt-1">{t('auth.resetPassword.successDesc')}</p>
              </div>
              <Link
                to="/signin"
                className="w-full bg-amber-500 hover:bg-amber-600 text-white font-bold py-3.5 px-4 rounded-xl transition-colors duration-200 shadow-sm shadow-amber-500/30 flex items-center justify-center"
              >
                {t('auth.resetPassword.signInNow')}
              </Link>
            </div>
          ) : (
            !error.includes('유효하지 않은') && !error.includes('Invalid access') && (
              <form onSubmit={handleSubmit} className="space-y-5">
                {/* New password */}
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5" htmlFor="reset-password">
                    {t('auth.resetPassword.newPassword')}
                  </label>
                  <div className="relative">
                    <input
                      id="reset-password"
                      name="password"
                      type={showPassword ? 'text' : 'password'}
                      required
                      value={formData.password}
                      onChange={handleChange}
                      className="w-full px-4 py-3 pr-11 rounded-xl bg-slate-50 border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 focus:border-amber-500 transition-all duration-200"
                      placeholder={t('auth.resetPassword.newPasswordPlaceholder')}
                    />
                    <PasswordToggle show={showPassword} onToggle={() => setShowPassword(!showPassword)} />
                  </div>

                  {/* Password strength bar */}
                  {formData.password && (
                    <div className="mt-2 space-y-1.5">
                      <div className="flex gap-1">
                        {[1, 2, 3, 4, 5].map((bar) => (
                          <div
                            key={bar}
                            className={`h-1 flex-1 rounded-full transition-all duration-300 ${
                              bar <= strength.level ? strength.color : 'bg-slate-200'
                            }`}
                          />
                        ))}
                      </div>
                      <p className={`text-xs font-semibold ${
                        strength.level <= 2 ? 'text-red-500' :
                        strength.level <= 3 ? 'text-amber-500' : 'text-emerald-600'
                      }`}>
                        {t('auth.resetPassword.strength.label')} {strength.label}
                      </p>
                    </div>
                  )}

                  <p className="text-xs text-slate-400 mt-2">{t('auth.resetPassword.passwordHint')}</p>
                </div>

                {/* Confirm password */}
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1.5" htmlFor="reset-confirm">
                    {t('auth.resetPassword.confirmPassword')}
                  </label>
                  <div className="relative">
                    <input
                      id="reset-confirm"
                      name="confirmPassword"
                      type={showConfirm ? 'text' : 'password'}
                      required
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      className={`w-full px-4 py-3 pr-11 rounded-xl bg-slate-50 border focus:bg-white focus:outline-none focus:ring-2 focus:ring-amber-500/20 transition-all duration-200 ${
                        formData.confirmPassword && formData.password !== formData.confirmPassword
                          ? 'border-red-300 focus:border-red-400'
                          : formData.confirmPassword && formData.password === formData.confirmPassword
                          ? 'border-emerald-300 focus:border-emerald-400'
                          : 'border-slate-200 focus:border-amber-500'
                      }`}
                      placeholder={t('auth.resetPassword.confirmPasswordPlaceholder')}
                    />
                    <PasswordToggle show={showConfirm} onToggle={() => setShowConfirm(!showConfirm)} />
                  </div>
                  {formData.confirmPassword && formData.password === formData.confirmPassword && (
                    <p className="text-xs text-emerald-600 font-semibold mt-1.5 flex items-center gap-1">
                      <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M5 13l4 4L19 7" />
                      </svg>
                      {t('auth.resetPassword.passwordMatch')}
                    </p>
                  )}
                </div>

                {/* Validation error */}
                {validationError && (
                  <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium flex items-start gap-2">
                    <svg className="w-5 h-5 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                        d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span>{validationError}</span>
                  </div>
                )}

                <div className="pt-1">
                  <button
                    type="submit"
                    id="reset-password-submit"
                    disabled={loading}
                    className="w-full bg-amber-500 hover:bg-amber-600 text-white font-bold py-3.5 px-4 rounded-xl transition-colors duration-200 shadow-sm shadow-amber-500/30 flex items-center justify-center gap-2 disabled:opacity-70 disabled:cursor-not-allowed"
                  >
                    {loading ? (
                      <svg className="animate-spin h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                        <path className="opacity-75" fill="currentColor"
                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                      </svg>
                    ) : (
                      <>
                        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                            d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                        </svg>
                        {t('auth.resetPassword.submit')}
                      </>
                    )}
                  </button>
                </div>
              </form>
            )
          )}

          {!success && (
            <div className="mt-8 text-center">
              <Link to="/signin" className="text-sm font-medium text-amber-600 hover:text-amber-700 hover:underline transition-all flex items-center justify-center gap-1">
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                </svg>
                {t('auth.resetPassword.backToSignIn')}
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ResetPassword;
