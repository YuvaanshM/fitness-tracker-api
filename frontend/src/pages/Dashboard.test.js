import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { getDailySummary } from '../api/meals';
import { getNutritionTrend } from '../api/progress';
import { getMetrics } from '../api/user';
import { getWorkouts } from '../api/workouts';
import { useAuth } from '../auth/AuthContext';
import Dashboard from './Dashboard';

jest.mock('../api/meals');
jest.mock('../api/progress');
jest.mock('../api/user');
jest.mock('../api/workouts');
jest.mock('../auth/AuthContext');

function renderDashboard() {
  const queryClient = new QueryClient();
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>
    </QueryClientProvider>
  );
}

beforeEach(() => {
  useAuth.mockReturnValue({ user: { id: 1, username: 'alice', weight: 140, goal: 'LOSE' } });
  getMetrics.mockResolvedValue({ recommendedCalories: 2200, proteinTargetGrams: 170, tdee: 2600, bmr: 1800 });
  getDailySummary.mockResolvedValue({ totalCalories: 900, totalProtein: 60 });
  getNutritionTrend.mockResolvedValue([]);
  getWorkouts.mockResolvedValue([]);
});

test('renders daily targets and remaining calories once data loads', async () => {
  renderDashboard();

  expect(await screen.findByText('2200')).toBeInTheDocument();
  expect(await screen.findByText('1300')).toBeInTheDocument();
});

test('shows an empty state when no recent workouts exist', async () => {
  renderDashboard();

  expect(await screen.findByText(/no workouts logged this week yet/i)).toBeInTheDocument();
  expect(screen.getByRole('link', { name: /start a workout/i })).toHaveAttribute('href', '/workouts');
});
