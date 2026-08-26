import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { updateProfile } from '../api/user';
import { useAuth } from '../auth/AuthContext';
import Profile from './Profile';

jest.mock('../api/user');
jest.mock('../auth/AuthContext');

const baseUser = {
  username: 'alice',
  height: 165,
  weight: 140,
  activityLevel: 'MODERATELY_ACTIVE',
  goal: 'LOSE',
  goalWeightChangePerWeek: -1,
  dateOfBirth: '1996-05-15',
};

function renderProfile() {
  const queryClient = new QueryClient();
  return render(
    <QueryClientProvider client={queryClient}>
      <Profile />
    </QueryClientProvider>
  );
}

beforeEach(() => {
  useAuth.mockReturnValue({ user: baseUser, refreshUser: jest.fn().mockResolvedValue(baseUser) });
});

test('renders the profile in view mode with an edit action', () => {
  renderProfile();

  expect(screen.getByRole('heading', { name: 'alice' })).toBeInTheDocument();
  expect(screen.getByText('140 lb')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /edit profile/i })).toBeInTheDocument();
});

test('switches to an editable form and submits the update', async () => {
  updateProfile.mockResolvedValue({ ...baseUser, weight: 150 });

  renderProfile();
  userEvent.click(screen.getByRole('button', { name: /edit profile/i }));

  expect(screen.getByRole('heading', { name: /edit your profile/i })).toBeInTheDocument();

  const weightInput = screen.getByLabelText(/weight \(lb\)/i);
  userEvent.clear(weightInput);
  userEvent.type(weightInput, '150');
  userEvent.click(screen.getByRole('button', { name: /save changes/i }));

  expect(await screen.findByRole('heading', { name: 'alice' })).toBeInTheDocument();
  expect(updateProfile.mock.calls[0][0]).toEqual(expect.objectContaining({ weight: 150 }));
});
