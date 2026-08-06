import { Link } from 'react-router-dom';
import Brand from '../components/Brand';

export default function Home() {
  return (
    <main className="relative min-h-screen overflow-hidden bg-cream">
      <div className="absolute -right-24 -top-24 h-96 w-96 rounded-full bg-brand-100 blur-3xl" aria-hidden="true" />
      <div className="absolute -bottom-24 -left-24 h-80 w-80 rounded-full bg-amber-100/70 blur-3xl" aria-hidden="true" />

      <nav className="relative mx-auto flex max-w-7xl items-center justify-between px-6 py-6" aria-label="Public navigation">
        <Brand />
        <Link to="/login" className="button-secondary py-2">
          Log in
        </Link>
      </nav>

      <section className="relative mx-auto grid max-w-7xl items-center gap-14 px-6 pb-20 pt-14 lg:grid-cols-2 lg:pb-28 lg:pt-24">
        <div>
          <p className="mb-5 text-sm font-black uppercase tracking-[0.22em] text-brand-700">
            Your health, made visible
          </p>
          <h1 className="max-w-2xl text-5xl font-black leading-[1.03] tracking-tight text-ink sm:text-6xl lg:text-7xl">
            Build momentum.
            <span className="block text-brand-600">Track every stride.</span>
          </h1>
          <p className="mt-7 max-w-xl text-lg leading-8 text-black/65">
            Bring workouts, nutrition, and personal goals into one focused dashboard built to help
            you make progress that lasts.
          </p>
          <div className="mt-9 flex flex-wrap gap-3">
            <Link to="/register" className="button-primary">
              Start tracking free
            </Link>
            <Link to="/login" className="button-secondary">
              I have an account
            </Link>
          </div>
        </div>

        <div className="relative mx-auto w-full max-w-lg" aria-label="Fitness dashboard preview">
          <div className="rounded-[2rem] border border-white/80 bg-white/75 p-5 shadow-card backdrop-blur sm:p-7">
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm font-bold text-black/45">Today</p>
                <h2 className="mt-1 text-2xl font-black">Keep the streak alive</h2>
              </div>
              <span className="rounded-full bg-brand-100 px-3 py-1 text-xs font-black text-brand-700">DAY 12</span>
            </div>
            <div className="mt-7 grid grid-cols-2 gap-4">
              <div className="rounded-2xl bg-brand-900 p-5 text-white">
                <p className="text-xs font-bold uppercase tracking-wider text-brand-100">Calories left</p>
                <p className="mt-3 text-4xl font-black">640</p>
                <div className="mt-5 h-2 overflow-hidden rounded-full bg-white/20">
                  <div className="h-full w-3/4 rounded-full bg-white" />
                </div>
              </div>
              <div className="rounded-2xl bg-amber-100 p-5">
                <p className="text-xs font-bold uppercase tracking-wider text-amber-900/60">Protein</p>
                <p className="mt-3 text-4xl font-black text-amber-950">112g</p>
                <p className="mt-5 text-sm font-semibold text-amber-950/60">28g to target</p>
              </div>
            </div>
            <div className="mt-4 rounded-2xl border border-black/5 bg-white p-5">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-xs font-bold uppercase tracking-wider text-black/45">Next workout</p>
                  <p className="mt-1 text-lg font-black">Push day</p>
                </div>
                <span className="grid h-12 w-12 place-items-center rounded-full bg-brand-100 text-xl" aria-hidden="true">→</span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
