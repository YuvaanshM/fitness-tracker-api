import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';

const routeTitles = {
  '/': 'Stride — Fitness tracking',
  '/login': 'Log in — Stride',
  '/register': 'Create account — Stride',
  '/dashboard': 'Dashboard — Stride',
  '/workouts': 'Workouts — Stride',
  '/food': 'Food — Stride',
  '/insights': 'Insights — Stride',
  '/profile': 'Profile — Stride',
};

export default function RouteAccessibility() {
  const location = useLocation();
  const previousPath = useRef(location.pathname);

  useEffect(() => {
    document.title = routeTitles[location.pathname] || 'Stride';

    if (previousPath.current !== location.pathname) {
      const heading = document.querySelector('main h1');

      if (heading) {
        const hadTabIndex = heading.hasAttribute('tabindex');
        heading.setAttribute('tabindex', '-1');
        heading.focus();

        if (!hadTabIndex) {
          heading.addEventListener('blur', () => heading.removeAttribute('tabindex'), { once: true });
        }
      }
    }

    previousPath.current = location.pathname;
  }, [location.pathname]);

  return null;
}
