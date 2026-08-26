import apiClient from './client';

export async function createMeal(payload) {
  const { data } = await apiClient.post('/api/meals', payload);
  return data;
}

export async function createMealFromFood(payload) {
  const { data } = await apiClient.post('/api/meals/from-food', payload);
  return data;
}

export async function getDailySummary(date) {
  const { data } = await apiClient.get('/api/meals/summary', { params: { date } });
  return data;
}

export async function getMealsByDate(date) {
  const { data } = await apiClient.get('/api/meals', { params: { date } });
  return data;
}
