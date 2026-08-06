import apiClient from './client';

export async function getMetrics() {
  const { data } = await apiClient.get('/api/user/metrics');
  return data;
}
