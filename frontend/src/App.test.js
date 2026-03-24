import { render, screen } from '@testing-library/react';
import App from './App';

test('renders test page heading', () => {
  render(<App />);
  const heading = screen.getByText(/Fitness Tracker API Test Page/i);
  expect(heading).toBeInTheDocument();
});
