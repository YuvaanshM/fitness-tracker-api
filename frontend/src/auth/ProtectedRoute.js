import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <main className="grid min-h-screen place-items-center bg-cream" aria-busy="true">
        <div className="flex items-center gap-3 text-sm font-semibold text-brand-900" role="status">
          <span className="h-5 w-5 animate-spin rounded-full border-2 border-brand-100 border-t-brand-600" />
          Loading your account
        </div>
      </main>
    );
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} />;
}
