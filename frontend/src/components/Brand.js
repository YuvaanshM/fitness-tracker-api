import { Link } from 'react-router-dom';

export default function Brand({ compact = false, light = false }) {
  return (
    <Link
      to="/"
      className={`inline-flex items-center gap-3 rounded-lg focus:outline-none focus-visible:ring-2 ${
        light ? 'focus-visible:ring-white' : 'focus-visible:ring-brand-600'
      }`}
      aria-label="Stride home"
    >
      <span
        className={`grid h-10 w-10 place-items-center rounded-xl ${
          light ? 'bg-white text-brand-700' : 'bg-brand-600 text-white'
        }`}
        aria-hidden="true"
      >
        <svg viewBox="0 0 24 24" className="h-6 w-6" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M5 15c3.5 0 4-6 7.5-6 2.5 0 2.5 3 5.5 3" strokeLinecap="round" />
          <path d="M6 8h3M15 16h3" strokeLinecap="round" />
        </svg>
      </span>
      {!compact && (
        <span className={`text-xl font-black tracking-tight ${light ? 'text-white' : 'text-ink'}`}>
          stride
        </span>
      )}
    </Link>
  );
}
