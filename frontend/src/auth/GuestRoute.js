import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function GuestRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <main className="grid min-h-screen place-items-center bg-cream" aria-busy="true">
        <p className="text-sm font-semibold text-brand-900" role="status">Loading your account…</p>
      </main>
    );
  }

  return isAuthenticated ? <Navigate to="/dashboard" replace /> : <Outlet />;
}
