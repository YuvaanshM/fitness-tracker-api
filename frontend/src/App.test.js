import { render, screen } from '@testing-library/react';
import App from './App';
import queryClient from './lib/queryClient';

afterEach(() => {
  localStorage.clear();
  queryClient.clear();
  window.history.pushState({}, '', '/');
});

test('renders the public landing page', () => {
  render(<App />);
  expect(screen.getByRole('heading', { name: /build momentum/i })).toBeInTheDocument();
  expect(screen.getByRole('link', { name: /start tracking free/i })).toHaveAttribute('href', '/register');
});

test('redirects signed-out visitors away from protected pages', async () => {
  window.history.pushState({}, '', '/dashboard?view=week');
  render(<App />);

  expect(await screen.findByRole('heading', { name: /welcome back/i })).toBeInTheDocument();
  expect(window.location.pathname).toBe('/login');
});
