import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { getApiErrorMessage } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import AuthShell from '../components/AuthShell';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const previousLocation = location.state?.from;
  const destination = previousLocation
    ? `${previousLocation.pathname}${previousLocation.search || ''}${previousLocation.hash || ''}`
    : '/dashboard';

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);

    try {
      await login(form);
      navigate(destination, { replace: true });
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'We could not sign you in with those details.'));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      title="Welcome back"
      subtitle="Log in to continue tracking your progress."
      footer={
        <p>
          New to Stride?{' '}
          <Link className="font-bold text-brand-700 hover:text-brand-900" to="/register">
            Create an account
          </Link>
        </p>
      }
    >
      <form className="space-y-5" onSubmit={handleSubmit}>
        {error && (
          <div className="error-banner" role="alert">
            {error}
          </div>
        )}
        <div>
          <label className="field-label" htmlFor="username">Username</label>
          <input
            className="field-input"
            id="username"
            name="username"
            autoComplete="username"
            required
            maxLength={50}
            value={form.username}
            onChange={(event) => setForm((current) => ({ ...current, username: event.target.value }))}
          />
        </div>
        <div>
          <label className="field-label" htmlFor="password">Password</label>
          <input
            className="field-input"
            id="password"
            name="password"
            type="password"
            autoComplete="current-password"
            required
            maxLength={128}
            value={form.password}
            onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
          />
        </div>
        <button
          className="button-primary w-full"
          type="submit"
          disabled={isSubmitting}
          aria-busy={isSubmitting}
        >
          {isSubmitting ? 'Logging in…' : 'Log in'}
        </button>
      </form>
    </AuthShell>
  );
}
