import apiClient from './client';

export async function logWeight(payload) {
  const { data } = await apiClient.post('/api/progress/weight', payload);
  return data;
}

export async function getWeightTrend(from, to) {
  const { data } = await apiClient.get('/api/progress/weight', { params: { from, to } });
  return data;
}

export async function getNutritionTrend(from, to) {
  const { data } = await apiClient.get('/api/progress/nutrition', { params: { from, to } });
  return data;
}

export async function getStrengthTrend(exerciseId, from, to) {
  const { data } = await apiClient.get(`/api/progress/exercise/${exerciseId}`, { params: { from, to } });
  return data;
}
