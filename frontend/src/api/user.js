import apiClient from './client';

export async function getMetrics() {
  const { data } = await apiClient.get('/api/user/metrics');
  return data;
}

export async function updateProfile(payload) {
  const { data } = await apiClient.put('/api/user/me', payload);
  return data;
}
