import apiClient from './client';

export async function loginUser(credentials) {
  const { data } = await apiClient.post('/api/auth/login', credentials);
  return data;
}

export async function registerUser(profile) {
  const { data } = await apiClient.post('/api/auth/register', profile);
  return data;
}

export async function getCurrentUser() {
  const { data } = await apiClient.get('/api/user/me');
  return data;
}
