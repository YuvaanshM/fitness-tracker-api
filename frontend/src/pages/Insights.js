import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { getApiErrorMessage } from '../api/client';
import { listExercises } from '../api/exercises';
import { getNutritionTrend, getStrengthTrend, getWeightTrend, logWeight } from '../api/progress';
import { getMetrics } from '../api/user';
import { useAuth } from '../auth/AuthContext';

function toIsoDate(date) {
  return date.toISOString().slice(0, 10);
}

function daysAgo(n) {
  return toIsoDate(new Date(Date.now() - n * 24 * 60 * 60 * 1000));
}

function formatShortDate(value) {
  return new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric' }).format(new Date(value));
}

function MetricsPanel({ metrics, isError, onRetry }) {
  return (
    <article className="rounded-3xl bg-ink p-6 text-white shadow-card sm:p-8">
      <p className="text-xs font-black uppercase tracking-[0.16em] text-white/45">Energy targets</p>
      <h2 className="mt-2 text-2xl font-black">BMR &amp; TDEE</h2>
      {isError ? (
        <div className="mt-6 text-sm text-white/60">
          Your metrics are unavailable right now.
          <button className="ml-2 font-black underline" onClick={onRetry}>Try again</button>
        </div>
      ) : (
        <dl className="mt-8 space-y-5">
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <dt className="text-sm text-white/60">Basal metabolic rate</dt>
            <dd className="font-black">{metrics?.bmr ?? '—'} kcal</dd>
          </div>
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <dt className="text-sm text-white/60">Total daily expenditure</dt>
            <dd className="font-black">{metrics?.tdee ?? '—'} kcal</dd>
          </div>
          <div className="flex items-center justify-between border-b border-white/10 pb-4">
            <dt className="text-sm text-white/60">Recommended calories</dt>
            <dd className="font-black">{metrics?.recommendedCalories ?? '—'} kcal</dd>
          </div>
          <div className="flex items-center justify-between">
            <dt className="text-sm text-white/60">Protein target</dt>
            <dd className="font-black">{metrics?.proteinTargetGrams ?? '—'} g</dd>
          </div>
        </dl>
      )}
    </article>
  );
}

function WeightTrendCard({ from, to }) {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [entryDate, setEntryDate] = useState(toIsoDate(new Date()));
  const [weightLbs, setWeightLbs] = useState('');

  const trendQuery = useQuery({
    queryKey: ['progress', user?.id, 'weight', from, to],
    queryFn: () => getWeightTrend(from, to),
    enabled: Boolean(user?.id),
  });

  const logWeightMutation = useMutation({
    mutationFn: logWeight,
    onSuccess: () => {
      setWeightLbs('');
      queryClient.invalidateQueries({ queryKey: ['progress', user?.id, 'weight'] });
    },
  });

  function handleSubmit(event) {
    event.preventDefault();
    logWeightMutation.mutate({ entryDate, weightLbs: Number(weightLbs) });
  }

  const chartData = (trendQuery.data ?? []).map((entry) => ({
    date: formatShortDate(entry.entryDate),
    weightLbs: entry.weightLbs,
  }));

  return (
    <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <h2 className="text-xl font-black">Weight trend</h2>

      <form className="mt-4 flex flex-wrap items-end gap-3" onSubmit={handleSubmit}>
        <div>
          <label className="field-label" htmlFor="weight-date">Date</label>
          <input id="weight-date" className="field-input" type="date" value={entryDate} onChange={(event) => setEntryDate(event.target.value)} />
        </div>
        <div>
          <label className="field-label" htmlFor="weight-value">Weight (lb)</label>
          <input
            id="weight-value"
            className="field-input w-32"
            type="number"
            min="0"
            step="0.1"
            required
            value={weightLbs}
            onChange={(event) => setWeightLbs(event.target.value)}
          />
        </div>
        <button className="button-secondary" type="submit" disabled={logWeightMutation.isPending}>
          {logWeightMutation.isPending ? 'Saving…' : 'Add entry'}
        </button>
      </form>
      {logWeightMutation.isError && (
        <div className="error-banner mt-4" role="alert">
          {getApiErrorMessage(logWeightMutation.error, 'We could not save that weight entry.')}
        </div>
      )}

      {trendQuery.isError ? (
        <div className="error-banner mt-6" role="alert">Weight history is unavailable right now.</div>
      ) : chartData.length === 0 ? (
        <p className="mt-6 text-sm text-black/55">No weight entries in this range yet.</p>
      ) : (
        <div className="mt-6 h-56">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(21,34,27,0.08)" />
              <XAxis dataKey="date" tickLine={false} axisLine={false} tick={{ fontSize: 12 }} />
              <YAxis tickLine={false} axisLine={false} tick={{ fontSize: 12 }} width={36} domain={['auto', 'auto']} />
              <Tooltip formatter={(value) => [`${value} lb`, 'Weight']} />
              <Line type="monotone" dataKey="weightLbs" stroke="#258848" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </article>
  );
}

function NutritionTrendCard({ from, to }) {
  const { user } = useAuth();
  const trendQuery = useQuery({
    queryKey: ['progress', user?.id, 'nutrition', from, to],
    queryFn: () => getNutritionTrend(from, to),
    enabled: Boolean(user?.id),
  });

  const chartData = (trendQuery.data ?? []).map((entry) => ({
    date: formatShortDate(entry.date),
    calories: entry.calories,
  }));

  return (
    <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <h2 className="text-xl font-black">Nutrition trend</h2>
      {trendQuery.isError ? (
        <div className="error-banner mt-6" role="alert">Nutrition history is unavailable right now.</div>
      ) : chartData.every((entry) => !entry.calories) ? (
        <p className="mt-6 text-sm text-black/55">No nutrition data in this range yet.</p>
      ) : (
        <div className="mt-6 h-56">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(21,34,27,0.08)" />
              <XAxis dataKey="date" tickLine={false} axisLine={false} tick={{ fontSize: 12 }} />
              <YAxis tickLine={false} axisLine={false} tick={{ fontSize: 12 }} width={36} />
              <Tooltip formatter={(value) => [`${value} kcal`, 'Calories']} />
              <Line type="monotone" dataKey="calories" stroke="#eab308" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </article>
  );
}

function StrengthTrendCard({ from, to }) {
  const { user } = useAuth();
  const [exerciseId, setExerciseId] = useState('');

  const exercisesQuery = useQuery({
    queryKey: ['exercises'],
    queryFn: () => listExercises(),
    enabled: Boolean(user?.id),
  });

  const strengthQuery = useQuery({
    queryKey: ['progress', user?.id, 'strength', exerciseId, from, to],
    queryFn: () => getStrengthTrend(exerciseId, from, to),
    enabled: Boolean(user?.id) && Boolean(exerciseId),
  });

  const chartData = (strengthQuery.data ?? []).map((entry) => ({
    date: formatShortDate(entry.performedAt),
    topWeightLbs: entry.topWeightLbs,
    estimatedOneRepMaxLbs: entry.estimatedOneRepMaxLbs,
  }));

  return (
    <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
      <h2 className="text-xl font-black">Strength trend</h2>

      <div className="mt-4">
        <label className="field-label" htmlFor="strength-exercise">Exercise</label>
        <select
          id="strength-exercise"
          className="field-input"
          value={exerciseId}
          onChange={(event) => setExerciseId(event.target.value)}
        >
          <option value="">Select an exercise</option>
          {(exercisesQuery.data ?? []).map((exercise) => (
            <option key={exercise.id} value={exercise.id}>{exercise.name}</option>
          ))}
        </select>
      </div>

      {!exerciseId ? (
        <p className="mt-6 text-sm text-black/55">Pick an exercise to see its strength trend.</p>
      ) : strengthQuery.isError ? (
        <div className="error-banner mt-6" role="alert">Strength history is unavailable right now.</div>
      ) : chartData.length === 0 ? (
        <p className="mt-6 text-sm text-black/55">No logged sets for this exercise in range yet.</p>
      ) : (
        <div className="mt-6 h-56">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(21,34,27,0.08)" />
              <XAxis dataKey="date" tickLine={false} axisLine={false} tick={{ fontSize: 12 }} />
              <YAxis tickLine={false} axisLine={false} tick={{ fontSize: 12 }} width={36} />
              <Tooltip />
              <Line type="monotone" dataKey="topWeightLbs" name="Top set (lb)" stroke="#258848" strokeWidth={2} dot={false} />
              <Line type="monotone" dataKey="estimatedOneRepMaxLbs" name="Est. 1RM (lb)" stroke="#dc2626" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      )}
    </article>
  );
}

export default function Insights() {
  const { user } = useAuth();
  const [from] = useState(daysAgo(90));
  const [to] = useState(toIsoDate(new Date()));

  const metrics = useQuery({
    queryKey: ['user', user?.id, 'metrics'],
    queryFn: getMetrics,
    enabled: Boolean(user?.id),
  });

  return (
    <div>
      <p className="text-sm font-black uppercase tracking-[0.18em] text-brand-700">Progress</p>
      <h1 className="mt-2 text-3xl font-black tracking-tight sm:text-4xl">Insights</h1>

      <section className="mt-8 grid gap-6 lg:grid-cols-[0.6fr_1.4fr]">
        <MetricsPanel metrics={metrics.data} isError={metrics.isError} onRetry={() => metrics.refetch()} />
        <NutritionTrendCard from={from} to={to} />
      </section>

      <section className="mt-6 grid gap-6 lg:grid-cols-2">
        <WeightTrendCard from={from} to={to} />
        <StrengthTrendCard from={from} to={to} />
      </section>
    </div>
  );
}
