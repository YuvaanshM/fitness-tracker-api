import apiClient from './client';

export async function getNutritionInsights() {
  const { data } = await apiClient.post('/api/ai/nutrition-insights');
  return data;
}

export async function parseMeal(text) {
  const { data } = await apiClient.post('/api/ai/parse-meal', { text });
  return data;
}

export async function getWorkoutSuggestion() {
  const { data } = await apiClient.post('/api/ai/workout-suggestion');
  return data;
}
