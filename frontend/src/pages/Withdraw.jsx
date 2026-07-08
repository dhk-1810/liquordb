import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { fetchAuthToken } from '../utils/auth';

function Withdraw() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [withdrawPassword, setWithdrawPassword] = useState('');
  const [isWithdrawing, setIsWithdrawing] = useState(false);

  const handleWithdrawSubmit = async (e) => {
    e.preventDefault();

    if (!window.confirm(t('mypage.confirmDeleteAccount'))) return;

    setIsWithdrawing(true);
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) {
        window.alert(t('mypage.loginRequired'));
        navigate('/signin');
        return;
      }

      const response = await fetch(`/api/users/${jwtData.userDto.id}?password=${encodeURIComponent(withdrawPassword)}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });

      if (!response.ok) {
        const err = await response.json().catch(() => ({}));
        throw new Error(err.message || 'Failed to delete account');
      }

      window.alert('회원 탈퇴 신청이 완료되었습니다. 1주일 이내 재접속 시 복구가 가능합니다.');
      try {
        await fetch('/api/auth/logout', { method: 'POST' });
      } catch (e) {
        console.error('Logout failed:', e);
      } finally {
        localStorage.removeItem('isLoggedIn');
        window.location.href = '/';
      }
    } catch (err) {
      console.error(err);
      window.alert(`탈퇴 처리 실패: ${err.message}`);
    } finally {
      setIsWithdrawing(false);
    }
  };

  return (
    <div className="max-w-md mx-auto my-12 px-4">
      <div className="bg-white rounded-3xl shadow-sm border border-slate-200 overflow-hidden animate-fade-in-up">
        <div className="px-8 py-6 border-b border-slate-200 bg-red-50/50">
          <h2 className="text-xl font-bold text-red-800">{t('mypage.deleteAccount')}</h2>
        </div>
        <form onSubmit={handleWithdrawSubmit} className="p-8 space-y-6">
          <div className="bg-red-50 border border-red-100 rounded-2xl p-5 text-sm text-red-700 leading-relaxed">
            <p className="font-bold mb-2">⚠️ 회원 탈퇴 안내 사항</p>
            <ul className="list-disc list-inside space-y-1">
              <li>탈퇴 신청 후 <strong>1주일 이내</strong>에는 다시 로그인하여 계정을 복구하실 수 있습니다.</li>
              <li>탈퇴 신청 1주일이 지나면 귀하의 계정 정보는 <strong>영구적으로 삭제</strong>되며 복구가 불가능합니다.</li>
              <li>영구 삭제 후에도 작성하신 리뷰와 댓글은 지워지지 않으며, <strong>'탈퇴한 사용자'</strong>로 이름을 대체하여 계속 표시됩니다.</li>
            </ul>
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-2">본인 확인 비밀번호</label>
            <input
              type="password"
              required
              placeholder="현재 비밀번호를 입력해 주세요"
              value={withdrawPassword}
              onChange={(e) => setWithdrawPassword(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-800 focus:bg-white focus:border-red-400 focus:ring-4 focus:ring-red-500/10 outline-none transition-all"
            />
          </div>

          <div className="flex gap-3 justify-end pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="px-6 py-3 text-slate-600 hover:bg-slate-100 rounded-xl font-semibold transition-colors"
            >
              {t('common.cancel') || '취소'}
            </button>
            <button
              type="submit"
              disabled={isWithdrawing}
              className="bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-8 rounded-xl transition-all shadow-md disabled:opacity-50"
            >
              {isWithdrawing ? '탈퇴 처리 중...' : '동의 및 탈퇴 신청'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Withdraw;
