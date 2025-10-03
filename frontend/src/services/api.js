export const getAuthToken = () => {
  return localStorage.getItem('jwt-token');
};

export const authenticatedFetch = async (url, options = {}) => {
  const token = getAuthToken();
  
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const newOptions = { ...options, headers };

  return fetch(url, newOptions);
};