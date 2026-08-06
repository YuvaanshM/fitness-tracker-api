import axios from 'axios';

export const TOKEN_STORAGE_KEY = 'fitness_tracker_token';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    config.authToken = token;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const storedToken = localStorage.getItem(TOKEN_STORAGE_KEY);
    const requestToken = error.config?.authToken;

    if (error.response?.status === 401 && requestToken && requestToken === storedToken) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      window.dispatchEvent(new Event('auth:unauthorized'));
    }

    return Promise.reject(error);
  }
);

export function getApiErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  const data = error.response?.data;

  if (typeof data === 'string' && data.trim()) {
    return data;
  }

  return data?.message || data?.error || fallback;
}

export default apiClient;
