import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { listExercises } from '../api/exercises';
import { getNutritionTrend, getWeightTrend } from '../api/progress';
import { getMetrics } from '../api/user';
import { useAuth } from '../auth/AuthContext';
import Insights from './Insights';

jest.mock('../api/exercises');
jest.mock('../api/progress');
jest.mock('../api/user');
jest.mock('../auth/AuthContext');

function renderInsights() {
  const queryClient = new QueryClient();
  return render(
    <QueryClientProvider client={queryClient}>
      <Insights />
    </QueryClientProvider>
  );
}

beforeEach(() => {
  useAuth.mockReturnValue({ user: { id: 1, username: 'alice' } });
  getMetrics.mockResolvedValue({ bmr: 1800, tdee: 2600, recommendedCalories: 2200, proteinTargetGrams: 170 });
  getWeightTrend.mockResolvedValue([]);
  getNutritionTrend.mockResolvedValue([]);
  listExercises.mockResolvedValue([{ id: 1, name: 'Bench Press' }]);
});

test('renders BMR/TDEE metrics once loaded', async () => {
  renderInsights();

  expect(await screen.findByText('1800 kcal')).toBeInTheDocument();
  expect(screen.getByText('2600 kcal')).toBeInTheDocument();
});

test('prompts for an exercise selection before showing strength trend', async () => {
  renderInsights();

  expect(await screen.findByText(/pick an exercise to see its strength trend/i)).toBeInTheDocument();
  expect(await screen.findByRole('option', { name: 'Bench Press' })).toBeInTheDocument();
});
