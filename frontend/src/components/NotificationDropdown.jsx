import { useState, useEffect, useRef } from 'react';
import { fetchAuthToken } from '../utils/auth';
import { useNavigate } from 'react-router-dom';

function NotificationDropdown() {
  const [notifications, setNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  const fetchNotifications = async () => {
    try {
      const jwtData = await fetchAuthToken();
      if (!jwtData) return;
      
      const response = await fetch('/api/notifications?limit=20', {
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setNotifications(data);
        setUnreadCount(data.filter(n => !n.isRead).length);
      }
    } catch (err) {
      console.error('Failed to fetch notifications:', err);
    }
  };

  useEffect(() => {
    fetchNotifications();

    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);

    // SSE 구독 설정
    let eventSource = null;
    let isCancelled = false;
    const setupSse = async () => {
      try {
        const jwtData = await fetchAuthToken();
        if (isCancelled) return;
        if (!jwtData) return;

        // Native EventSource 생성 (인증 토큰을 쿼리 스트링으로 전달)
        eventSource = new EventSource(`/api/sse?token=${jwtData.accessToken}`);

        // 알림 수신 시 핸들러 등록
        eventSource.addEventListener('notification', (event) => {
          try {
            const newNotification = JSON.parse(event.data);
            
            // 중복 알림 등록 방지 및 상태 갱신
            setNotifications(prev => {
              if (prev.some(n => n.id === newNotification.id)) return prev;
              setUnreadCount(prevUnread => prevUnread + 1);
              return [newNotification, ...prev];
            });
          } catch (e) {
            console.error('Error parsing SSE notification:', e);
          }
        });

        eventSource.onerror = (err) => {
          console.error('SSE connection error, closing...', err);
          eventSource.close();
        };
      } catch (err) {
        console.error('Failed to setup SSE:', err);
      }
    };

    setupSse();

    return () => {
      isCancelled = true;
      document.removeEventListener('mousedown', handleClickOutside);
      if (eventSource) {
        eventSource.close();
      }
    };
  }, []);

  const handleToggle = () => {
    const nextState = !isOpen;
    setIsOpen(nextState);
    if (nextState) {
      // Refresh on open
      fetchNotifications();
    }
  };

  const handleMarkAsRead = async (id, e) => {
    e.stopPropagation(); // prevent triggering outer click if we make the whole row clickable
    try {
      const jwtData = await fetchAuthToken();
      await fetch(`/api/notifications/${id}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });
      // Update local state
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (err) {
      console.error(err);
    }
  };

  const handleClearAll = async () => {
    try {
      const jwtData = await fetchAuthToken();
      await fetch('/api/notifications', {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${jwtData.accessToken}`
        }
      });
      setNotifications([]);
      setUnreadCount(0);
      setIsOpen(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleNotificationClick = (notification) => {
    if (!notification.isRead) {
      handleMarkAsRead(notification.id, { stopPropagation: () => {} });
    }
    setIsOpen(false);
    // Usually a notification might have a target URL in its content or we infer it. 
    // If not, we just mark as read. If we had a link, we'd navigate.
  };

  return (
    <div className="relative ml-2" ref={dropdownRef}>
      <button 
        onClick={handleToggle}
        className="relative p-2 rounded-full hover:bg-slate-100 transition-colors focus:outline-none flex items-center justify-center text-slate-500 hover:text-amber-600"
      >
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center border-2 border-white shadow-sm">
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 bg-white rounded-2xl shadow-xl border border-slate-200 overflow-hidden z-50 transform origin-top-right transition-all">
          <div className="flex items-center justify-between px-4 py-3 border-b border-slate-100 bg-slate-50">
            <h3 className="font-bold text-slate-800">Notifications</h3>
            {notifications.length > 0 && (
              <button 
                onClick={handleClearAll}
                className="text-xs font-semibold text-slate-400 hover:text-red-500 transition-colors"
              >
                Clear All
              </button>
            )}
          </div>
          
          <div className="max-h-96 overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="py-8 text-center text-slate-500 text-sm">
                No new notifications
              </div>
            ) : (
              <ul className="divide-y divide-slate-100">
                {notifications.map(notification => (
                  <li 
                    key={notification.id} 
                    onClick={() => handleNotificationClick(notification)}
                    className={`p-4 hover:bg-slate-50 cursor-pointer transition-colors ${!notification.isRead ? 'bg-amber-50/30' : ''}`}
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex-grow">
                        <p className={`text-sm ${!notification.isRead ? 'font-bold text-slate-800' : 'font-medium text-slate-600'}`}>
                          {notification.title}
                        </p>
                        <p className="text-xs text-slate-500 mt-1 line-clamp-2">{notification.content}</p>
                        <p className="text-[10px] text-slate-400 mt-2 font-medium">
                          {new Date(notification.createdAt).toLocaleString()}
                        </p>
                      </div>
                      {!notification.isRead && (
                        <div className="w-2 h-2 rounded-full bg-amber-500 flex-shrink-0 mt-1"></div>
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default NotificationDropdown;
