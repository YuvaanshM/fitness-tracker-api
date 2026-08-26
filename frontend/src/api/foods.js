import apiClient from './client';

export async function searchFoods(query, pageSize = 25) {
  const { data } = await apiClient.get('/api/foods/search', { params: { query, pageSize } });
  return data;
}

export async function getFoodDetail(fdcId) {
  const { data } = await apiClient.get(`/api/foods/${fdcId}`);
  return data;
}
