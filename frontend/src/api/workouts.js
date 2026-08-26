import apiClient from './client';

export async function startWorkout(payload) {
  const { data } = await apiClient.post('/api/workouts', payload);
  return data;
}

export async function logSet(sessionId, payload) {
  const { data } = await apiClient.post(`/api/workouts/${sessionId}/sets`, payload);
  return data;
}

export async function finishWorkout(sessionId, payload) {
  const { data } = await apiClient.patch(`/api/workouts/${sessionId}`, payload);
  return data;
}

export async function getWorkouts({ from, to } = {}) {
  const { data } = await apiClient.get('/api/workouts', { params: { from, to } });
  return data;
}

export async function getWorkout(sessionId) {
  const { data } = await apiClient.get(`/api/workouts/${sessionId}`);
  return data;
}
