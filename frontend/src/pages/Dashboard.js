import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { getDailySummary } from '../api/meals';
import { getNutritionTrend } from '../api/progress';
import { getMetrics } from '../api/user';
import { getWorkouts } from '../api/workouts';
import { useAuth } from '../auth/AuthContext';

function MetricCard({ label, value, unit, tone = 'light' }) {
  const tones = {
    light: 'bg-white text-ink',
    green: 'bg-brand-900 text-white',
    warm: 'bg-amber-100 text-amber-950',
  };

  return (
    <article className={`rounded-3xl p-6 shadow-card ${tones[tone]}`}>
      <p className={`text-xs font-black uppercase tracking-[0.16em] ${tone === 'green' ? 'text-brand-100' : 'opacity-50'}`}>
        {label}
      </p>
      <p className="mt-3 text-4xl font-black">
        {value ?? '—'}
        {value != null && <span className="ml-1 text-base font-bold opacity-60">{unit}</span>}
      </p>
    </article>
  );
}

function toIsoDate(date) {
  return date.toISOString().slice(0, 10);
}

function lastNDays(n) {
  const today = new Date();
  return Array.from({ length: n }, (_, index) => {
    const date = new Date(today);
    date.setDate(today.getDate() - (n - 1 - index));
    return toIsoDate(date);
  });
}

export default function Dashboard() {
  const { user } = useAuth();
  const today = toIsoDate(new Date());
  const weekDates = lastNDays(7);

  const metrics = useQuery({
    queryKey: ['user', user?.id, 'metrics'],
    queryFn: getMetrics,
    enabled: Boolean(user?.id),
  });

  const dailySummary = useQuery({
    queryKey: ['meals', user?.id, 'summary', today],
    queryFn: () => getDailySummary(today),
    enabled: Boolean(user?.id),
  });

  const nutritionTrend = useQuery({
    queryKey: ['progress', user?.id, 'nutrition', weekDates[0], weekDates[6]],
    queryFn: () => getNutritionTrend(weekDates[0], weekDates[6]),
    enabled: Boolean(user?.id),
  });

  const recentWorkouts = useQuery({
    queryKey: ['workouts', user?.id, 'recent', weekDates[0], today],
    queryFn: () => getWorkouts({ from: weekDates[0], to: today }),
    enabled: Boolean(user?.id),
  });

  const recommendedCalories = metrics.data?.recommendedCalories;
  const consumedCalories = dailySummary.data?.totalCalories;
  const remainingCalories =
    recommendedCalories != null && consumedCalories != null
      ? Math.round(recommendedCalories - consumedCalories)
      : null;

  const chartData = weekDates.map((date) => {
    const entry = nutritionTrend.data?.find((row) => row.date === date);
    return {
      date,
      label: new Intl.DateTimeFormat('en-US', { weekday: 'short' }).format(new Date(`${date}T00:00:00`)),
      calories: entry?.calories ?? 0,
    };
  });

  const latestWorkout = [...(recentWorkouts.data ?? [])].sort(
    (a, b) => new Date(b.startedAt) - new Date(a.startedAt)
  )[0];

  return (
    <div>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-sm font-black uppercase tracking-[0.18em] text-brand-700">Your overview</p>
          <h1 className="mt-2 text-3xl font-black tracking-tight sm:text-4xl">
            Welcome back, {user?.username}
          </h1>
        </div>
        <p className="text-sm font-semibold text-black/50">
          {new Intl.DateTimeFormat('en-US', { weekday: 'long', month: 'long', day: 'numeric' }).format(new Date())}
        </p>
      </div>

      {metrics.isError && (
        <div className="error-banner mt-8" role="alert">
          Your calculated targets are unavailable right now.
          <button className="ml-2 font-black underline" onClick={() => metrics.refetch()}>Try again</button>
        </div>
      )}

      <section className="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4" aria-label="Daily targets">
        <MetricCard
          label="Daily calorie target"
          value={metrics.data?.recommendedCalories}
          unit="kcal"
          tone="green"
        />
        <MetricCard label="Protein target" value={metrics.data?.proteinTargetGrams} unit="g" tone="warm" />
        <MetricCard label="Daily expenditure" value={metrics.data?.tdee} unit="kcal" />
        <MetricCard label="Basal rate" value={metrics.data?.bmr} unit="kcal" />
      </section>

      <section className="mt-8 grid gap-4 lg:grid-cols-[1.4fr_0.6fr]">
        <article className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-8">
          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">Today’s progress</p>
              <h2 className="mt-2 text-2xl font-black">Nutrition at a glance</h2>
            </div>
            <Link to="/food" className="button-secondary py-2 text-xs">Log a meal</Link>
          </div>

          {dailySummary.isError ? (
            <div className="error-banner mt-8" role="alert">Today's nutrition totals are unavailable right now.</div>
          ) : (
            <div className="mt-8 grid gap-4 sm:grid-cols-3">
              <div className="rounded-2xl bg-brand-50 p-5">
                <p className="text-xs font-bold uppercase tracking-wide text-brand-700/70">Consumed</p>
                <p className="mt-2 text-2xl font-black text-brand-900">{consumedCalories ?? '—'} <span className="text-sm font-bold opacity-60">kcal</span></p>
              </div>
              <div className="rounded-2xl bg-black/5 p-5">
                <p className="text-xs font-bold uppercase tracking-wide text-black/50">Remaining</p>
                <p className="mt-2 text-2xl font-black">
                  {remainingCalories ?? '—'} <span className="text-sm font-bold opacity-60">kcal</span>
                </p>
              </div>
              <div className="rounded-2xl bg-amber-100 p-5">
                <p className="text-xs font-bold uppercase tracking-wide text-amber-950/70">Protein</p>
                <p className="mt-2 text-2xl font-black text-amber-950">
                  {dailySummary.data?.totalProtein ?? '—'} <span className="text-sm font-bold opacity-60">g</span>
                </p>
              </div>
            </div>
          )}

          <div className="mt-8">
            <p className="text-xs font-black uppercase tracking-[0.16em] text-black/45">This week</p>
            <div className="mt-4 h-56">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(21,34,27,0.08)" />
                  <XAxis dataKey="label" tickLine={false} axisLine={false} tick={{ fontSize: 12, fontWeight: 700 }} />
                  <YAxis tickLine={false} axisLine={false} tick={{ fontSize: 12 }} width={36} />
                  <Tooltip
                    formatter={(value) => [`${value} kcal`, 'Calories']}
                    contentStyle={{ borderRadius: 12, border: 'none', boxShadow: '0 12px 30px -20px rgba(21,34,27,0.4)' }}
                  />
                  <Bar dataKey="calories" fill="#258848" radius={[8, 8, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </article>

        <article className="rounded-3xl bg-ink p-6 text-white shadow-card sm:p-8">
          <p className="text-xs font-black uppercase tracking-[0.16em] text-white/45">Recent workout</p>
          {recentWorkouts.isError ? (
            <p className="mt-4 text-sm text-white/60">Workout status is unavailable right now.</p>
          ) : latestWorkout ? (
            <div className="mt-4">
              <h2 className="text-2xl font-black">{latestWorkout.routineDayName ?? 'Freeform session'}</h2>
              <p className="mt-1 text-sm text-white/60">
                {new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric' }).format(new Date(latestWorkout.startedAt))}
              </p>
              <span
                className={`mt-4 inline-flex rounded-full px-3 py-1 text-xs font-black ${
                  latestWorkout.endedAt ? 'bg-brand-500/20 text-brand-100' : 'bg-amber-500/20 text-amber-200'
                }`}
              >
                {latestWorkout.endedAt ? 'Completed' : 'In progress'}
              </span>
              <p className="mt-4 text-sm text-white/60">{latestWorkout.sets?.length ?? 0} sets logged</p>
            </div>
          ) : (
            <p className="mt-4 text-sm text-white/60">No workouts logged this week yet.</p>
          )}
          <Link to="/workouts" className="button-primary mt-6 w-full">
            {latestWorkout && !latestWorkout.endedAt ? 'Resume workout' : 'Start a workout'}
          </Link>

          <dl className="mt-8 space-y-5 border-t border-white/10 pt-6">
            <div className="flex items-center justify-between border-b border-white/10 pb-4">
              <dt className="text-sm text-white/60">Weight</dt>
              <dd className="font-black">{user?.weight} lb</dd>
            </div>
            <div className="flex items-center justify-between">
              <dt className="text-sm text-white/60">Goal</dt>
              <dd className="font-black capitalize">{user?.goal?.toLowerCase()}</dd>
            </div>
          </dl>
        </article>
      </section>
    </div>
  );
}
