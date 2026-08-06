import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getApiErrorMessage } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import AuthShell from '../components/AuthShell';

const initialForm = {
  username: '',
  password: '',
  sex: 'M',
  dateOfBirth: '',
  height: '',
  weight: '',
  activityLevel: 'MODERATELY_ACTIVE',
  goal: 'MAINTAIN',
  goalWeightChangePerWeek: 0,
};

const goalRates = {
  LOSE: -1,
  MAINTAIN: 0,
  GAIN: 1,
};

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function updateGoal(event) {
    const goal = event.target.value;
    setForm((current) => ({
      ...current,
      goal,
      goalWeightChangePerWeek: goalRates[goal],
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);

    const payload = {
      ...form,
      height: Number(form.height),
      weight: Number(form.weight),
      goalWeightChangePerWeek: Number(form.goalWeightChangePerWeek),
    };

    try {
      await register(payload);
      navigate('/dashboard', { replace: true });
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'We could not create your account.'));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthShell
      title="Create your account"
      subtitle="Tell us a little about you so your goals can be personalized."
      footer={
        <p>
          Already have an account?{' '}
          <Link className="font-bold text-brand-700 hover:text-brand-900" to="/login">
            Log in
          </Link>
        </p>
      }
    >
      <form className="space-y-5" onSubmit={handleSubmit}>
        {error && <div className="error-banner" role="alert">{error}</div>}

        <div className="grid gap-5 sm:grid-cols-2">
          <div className="sm:col-span-2">
            <label className="field-label" htmlFor="username">Username</label>
            <input
              className="field-input"
              id="username"
              name="username"
              autoComplete="username"
              required
              minLength={3}
              maxLength={50}
              value={form.username}
              onChange={updateField}
            />
          </div>
          <div className="sm:col-span-2">
            <label className="field-label" htmlFor="password">Password</label>
            <input
              className="field-input"
              id="password"
              name="password"
              type="password"
              autoComplete="new-password"
              required
              minLength={8}
              maxLength={128}
              aria-describedby="password-help"
              value={form.password}
              onChange={updateField}
            />
            <p id="password-help" className="mt-2 text-xs text-black/50">Use at least 8 characters.</p>
          </div>
          <div>
            <label className="field-label" htmlFor="sex">Sex used for metrics</label>
            <select className="field-input" id="sex" name="sex" value={form.sex} onChange={updateField}>
              <option value="M">Male</option>
              <option value="F">Female</option>
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="dateOfBirth">Date of birth</label>
            <input
              className="field-input"
              id="dateOfBirth"
              name="dateOfBirth"
              type="date"
              required
              max={new Date().toISOString().split('T')[0]}
              value={form.dateOfBirth}
              onChange={updateField}
            />
          </div>
          <div>
            <label className="field-label" htmlFor="height">Height (cm)</label>
            <input
              className="field-input"
              id="height"
              name="height"
              type="number"
              inputMode="decimal"
              required
              min="1"
              max="300"
              step="0.1"
              value={form.height}
              onChange={updateField}
            />
          </div>
          <div>
            <label className="field-label" htmlFor="weight">Weight (lb)</label>
            <input
              className="field-input"
              id="weight"
              name="weight"
              type="number"
              inputMode="decimal"
              required
              min="1"
              max="1000"
              step="0.1"
              value={form.weight}
              onChange={updateField}
            />
          </div>
          <div className="sm:col-span-2">
            <label className="field-label" htmlFor="activityLevel">Activity level</label>
            <select
              className="field-input"
              id="activityLevel"
              name="activityLevel"
              value={form.activityLevel}
              onChange={updateField}
            >
              <option value="SEDENTARY">Sedentary</option>
              <option value="LIGHTLY_ACTIVE">Lightly active</option>
              <option value="MODERATELY_ACTIVE">Moderately active</option>
              <option value="VERY_ACTIVE">Very active</option>
              <option value="EXTRA_ACTIVE">Extra active</option>
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="goal">Goal</label>
            <select className="field-input" id="goal" name="goal" value={form.goal} onChange={updateGoal}>
              <option value="LOSE">Lose weight</option>
              <option value="MAINTAIN">Maintain weight</option>
              <option value="GAIN">Gain weight</option>
            </select>
          </div>
          <div>
            <label className="field-label" htmlFor="goalWeightChangePerWeek">Weekly change (lb)</label>
            <input
              className="field-input"
              id="goalWeightChangePerWeek"
              name="goalWeightChangePerWeek"
              type="number"
              min="-10"
              max="10"
              step="0.25"
              required
              value={form.goalWeightChangePerWeek}
              onChange={updateField}
            />
          </div>
        </div>

        <button
          className="button-primary w-full"
          type="submit"
          disabled={isSubmitting}
          aria-busy={isSubmitting}
        >
          {isSubmitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
    </AuthShell>
  );
}
