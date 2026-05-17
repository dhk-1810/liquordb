export let refreshTokenPromise = null;

export const fetchAuthToken = async () => {
  if (localStorage.getItem('isLoggedIn') !== 'true') {
    return null;
  }

  if (refreshTokenPromise) {
    return refreshTokenPromise;
  }

  refreshTokenPromise = fetch('/api/auth/token-refresh', { method: 'POST' })
    .then(async (res) => {
      if (!res.ok) {
        localStorage.removeItem('isLoggedIn');
        throw new Error('Token refresh failed');
      }
      return await res.json(); // returns jwtData containing userDto and accessToken
    })
    .finally(() => {
      // Clear the promise so next time we call it, we make a new request
      // (This is useful if the token expires and we need to refresh again later)
      refreshTokenPromise = null;
    });

  return refreshTokenPromise;
};
