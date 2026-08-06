import { useEffect, useRef, useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import Brand from './Brand';

const navigation = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/workouts', label: 'Workouts' },
  { to: '/food', label: 'Food' },
  { to: '/insights', label: 'Insights' },
  { to: '/profile', label: 'Profile' },
];

function SidebarContent({ onNavigate }) {
  return (
    <>
      <Brand light />
      <nav className="mt-10" aria-label="Primary navigation">
        <ul className="space-y-1">
          {navigation.map((item) => (
            <li key={item.to}>
              <NavLink
                to={item.to}
                onClick={onNavigate}
                className={({ isActive }) =>
                  `block rounded-xl px-4 py-3 text-sm font-bold transition ${
                    isActive
                      ? 'bg-white text-brand-900 shadow-sm'
                      : 'text-brand-100 hover:bg-brand-700 hover:text-white'
                  }`
                }
              >
                {item.label}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </>
  );
}

export default function Layout() {
  const { user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);
  const closeButtonRef = useRef(null);
  const drawerRef = useRef(null);
  const location = useLocation();

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!menuOpen) {
      return undefined;
    }

    const previouslyFocused = document.activeElement;
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    closeButtonRef.current?.focus();

    function handleKeyDown(event) {
      if (event.key === 'Escape') {
        event.preventDefault();
        setMenuOpen(false);
        return;
      }

      if (event.key !== 'Tab') {
        return;
      }

      const focusable = drawerRef.current?.querySelectorAll(
        'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'
      );

      if (!focusable?.length) {
        event.preventDefault();
        return;
      }

      const first = focusable[0];
      const last = focusable[focusable.length - 1];

      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault();
        first.focus();
      }
    }

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = previousOverflow;
      previouslyFocused?.focus();
    };
  }, [menuOpen]);

  return (
    <div className="min-h-screen bg-cream text-ink">
      <a
        href="#main-content"
        className="sr-only z-50 rounded-lg bg-white px-4 py-2 font-bold text-brand-900 focus:not-sr-only focus:fixed focus:left-4 focus:top-4"
      >
        Skip to main content
      </a>

      <aside className="fixed inset-y-0 left-0 hidden w-64 bg-brand-900 p-6 lg:block">
        <SidebarContent />
      </aside>

      {menuOpen && (
        <div className="fixed inset-0 z-40 lg:hidden">
          <button
            className="absolute inset-0 bg-ink/50"
            onClick={() => setMenuOpen(false)}
            aria-label="Close navigation"
          />
          <aside
            id="mobile-navigation"
            ref={drawerRef}
            className="relative h-full w-72 bg-brand-900 p-6 shadow-2xl"
            aria-label="Mobile navigation"
            role="dialog"
            aria-modal="true"
          >
            <button
              ref={closeButtonRef}
              type="button"
              className="absolute right-4 top-4 rounded-lg p-2 text-white hover:bg-brand-700 focus:outline-none focus-visible:ring-2 focus-visible:ring-white"
              onClick={() => setMenuOpen(false)}
              aria-label="Close menu"
            >
              <span aria-hidden="true">✕</span>
            </button>
            <SidebarContent onNavigate={() => setMenuOpen(false)} />
          </aside>
        </div>
      )}

      <div className="lg:pl-64" aria-hidden={menuOpen ? 'true' : undefined} inert={menuOpen ? true : undefined}>
        <header className="sticky top-0 z-30 border-b border-black/5 bg-cream/90 px-4 py-4 backdrop-blur sm:px-8">
          <div className="mx-auto flex max-w-7xl items-center justify-between">
            <button
              type="button"
              className="rounded-lg p-2 text-brand-900 hover:bg-brand-100 focus:outline-none focus-visible:ring-2 focus-visible:ring-brand-600 lg:hidden"
              onClick={() => setMenuOpen(true)}
              aria-expanded={menuOpen}
              aria-controls="mobile-navigation"
              aria-label="Open navigation"
            >
              <span className="text-xl" aria-hidden="true">☰</span>
            </button>
            <div className="ml-auto flex items-center gap-4">
              <div className="hidden text-right sm:block">
                <p className="text-xs font-semibold uppercase tracking-wider text-black/50">Signed in as</p>
                <p className="text-sm font-bold">{user?.username}</p>
              </div>
              <button type="button" className="button-secondary py-2" onClick={() => logout()}>
                Log out
              </button>
            </div>
          </div>
        </header>

        <main id="main-content" className="mx-auto max-w-7xl p-4 sm:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
