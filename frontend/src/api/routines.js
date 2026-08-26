import apiClient from './client';

export async function createRoutine(payload) {
  const { data } = await apiClient.post('/api/routines', payload);
  return data;
}

export async function getRoutines() {
  const { data } = await apiClient.get('/api/routines');
  return data;
}

export async function getRoutine(routineId) {
  const { data } = await apiClient.get(`/api/routines/${routineId}`);
  return data;
}

export async function updateRoutine(routineId, payload) {
  const { data } = await apiClient.put(`/api/routines/${routineId}`, payload);
  return data;
}

export async function deleteRoutine(routineId) {
  await apiClient.delete(`/api/routines/${routineId}`);
}

export async function addRoutineDay(routineId, payload) {
  const { data } = await apiClient.post(`/api/routines/${routineId}/days`, payload);
  return data;
}

export async function addRoutineExercise(routineId, dayId, payload) {
  const { data } = await apiClient.post(`/api/routines/${routineId}/days/${dayId}/exercises`, payload);
  return data;
}
