import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { searchFoods } from '../api/foods';
import { getMealsByDate } from '../api/meals';
import { useAuth } from '../auth/AuthContext';
import Food from './Food';

jest.mock('../api/foods');
jest.mock('../api/meals');
jest.mock('../auth/AuthContext');

function renderFood() {
  const queryClient = new QueryClient();
  return render(
    <QueryClientProvider client={queryClient}>
      <Food />
    </QueryClientProvider>
  );
}

beforeEach(() => {
  useAuth.mockReturnValue({ user: { id: 1, username: 'alice' } });
  searchFoods.mockResolvedValue([]);
});

test('shows an empty state when no meals are logged for the selected date', async () => {
  getMealsByDate.mockResolvedValue([]);

  renderFood();

  expect(await screen.findByText(/no meals logged for this date yet/i)).toBeInTheDocument();
});

test('lists logged meals grouped by category', async () => {
  getMealsByDate.mockResolvedValue([
    { id: 1, name: 'Oatmeal', category: 'BREAKFAST', calories: 300, protein: 10, carbs: 50, fats: 5 },
  ]);

  renderFood();

  expect(await screen.findByText('Oatmeal')).toBeInTheDocument();
  expect(screen.getAllByText('BREAKFAST').length).toBeGreaterThan(0);
});
