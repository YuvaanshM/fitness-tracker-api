import { Link } from 'react-router-dom';
import Brand from './Brand';

export default function AuthShell({ title, subtitle, children, footer }) {
  return (
    <main className="grid min-h-screen bg-cream lg:grid-cols-[0.85fr_1.15fr]">
      <section className="hidden bg-brand-900 p-12 text-white lg:flex lg:flex-col lg:justify-between">
        <Brand light />
        <div>
          <p className="max-w-md text-4xl font-black leading-tight">
            Small choices become strong habits.
          </p>
          <p className="mt-5 max-w-md leading-7 text-brand-100">
            See your training and nutrition in one place, then use the data to keep moving forward.
          </p>
        </div>
        <p className="text-sm text-brand-100">A clearer path to your next personal best.</p>
      </section>

      <section className="flex items-center justify-center px-5 py-10 sm:px-10">
        <div className="w-full max-w-xl">
          <div className="mb-10 flex items-center justify-between lg:hidden">
            <Brand />
            <Link to="/" className="text-sm font-bold text-brand-700 hover:text-brand-900">
              Back home
            </Link>
          </div>
          <div className="rounded-3xl border border-black/5 bg-white p-6 shadow-card sm:p-10">
            <h1 className="text-3xl font-black tracking-tight sm:text-4xl">{title}</h1>
            <p className="mt-3 text-black/60">{subtitle}</p>
            <div className="mt-8">{children}</div>
            {footer && <div className="mt-7 border-t border-black/5 pt-6 text-sm text-black/60">{footer}</div>}
          </div>
        </div>
      </section>
    </main>
  );
}
