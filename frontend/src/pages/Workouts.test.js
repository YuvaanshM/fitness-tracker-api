import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { getRoutines } from '../api/routines';
import { getWorkouts } from '../api/workouts';
import { useAuth } from '../auth/AuthContext';
import Workouts from './Workouts';

jest.mock('../api/routines');
jest.mock('../api/workouts');
jest.mock('../auth/AuthContext');

function renderWorkouts() {
  const queryClient = new QueryClient();
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <Workouts />
      </MemoryRouter>
    </QueryClientProvider>
  );
}

beforeEach(() => {
  useAuth.mockReturnValue({ user: { id: 1, username: 'alice' } });
});

test('lets the user pick a routine day to start a workout', async () => {
  getRoutines.mockResolvedValue([
    { id: 5, name: 'Push Day', days: [{ id: 50, name: 'Day A', exercises: [] }] },
  ]);
  getWorkouts.mockResolvedValue([]);

  renderWorkouts();

  expect(await screen.findByRole('option', { name: 'Push Day' })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /start workout/i })).toBeDisabled();
});

test('shows an empty state when there is no workout history', async () => {
  getRoutines.mockResolvedValue([]);
  getWorkouts.mockResolvedValue([]);

  renderWorkouts();

  expect(await screen.findByText(/no past workouts yet/i)).toBeInTheDocument();
});
