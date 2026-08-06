import { useQuery } from '@tanstack/react-query';
import { getMetrics } from '../api/user';
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

export default function Dashboard() {
  const { user } = useAuth();
  const metrics = useQuery({
    queryKey: ['user', user?.id, 'metrics'],
    queryFn: getMetrics,
    enabled: Boolean(user?.id),
  });

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
            <span className="rounded-full bg-brand-100 px-3 py-1 text-xs font-black text-brand-700">COMING NEXT</span>
          </div>
          <div className="mt-8 grid h-52 place-items-center rounded-2xl border border-dashed border-brand-500/30 bg-brand-50">
            <p className="max-w-sm px-6 text-center text-sm leading-6 text-brand-900/65">
              Daily calorie and macro tracking will appear here when the dashboard phase is connected.
            </p>
          </div>
        </article>

        <article className="rounded-3xl bg-ink p-6 text-white shadow-card sm:p-8">
          <p className="text-xs font-black uppercase tracking-[0.16em] text-white/45">Profile baseline</p>
          <h2 className="mt-2 text-2xl font-black">Your starting point</h2>
          <dl className="mt-8 space-y-5">
            <div className="flex items-center justify-between border-b border-white/10 pb-4">
              <dt className="text-sm text-white/60">Weight</dt>
              <dd className="font-black">{user?.weight} lb</dd>
            </div>
            <div className="flex items-center justify-between border-b border-white/10 pb-4">
              <dt className="text-sm text-white/60">Height</dt>
              <dd className="font-black">{user?.height} cm</dd>
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
