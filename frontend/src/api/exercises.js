import apiClient from './client';

export async function listExercises({ query, muscle } = {}) {
  const { data } = await apiClient.get('/api/exercises', { params: { query, muscle } });
  return data;
}

export async function createExercise(payload) {
  const { data } = await apiClient.post('/api/exercises', payload);
  return data;
}
