import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCurrentUser, loginUser, registerUser } from '../api/auth';
import { TOKEN_STORAGE_KEY } from '../api/client';
import queryClient from '../lib/queryClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(() => Boolean(localStorage.getItem(TOKEN_STORAGE_KEY)));

  const logout = useCallback(
    (redirect = true) => {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      void queryClient.cancelQueries();
      queryClient.clear();
      setUser(null);
      setIsLoading(false);

      if (redirect) {
        navigate('/login', { replace: true });
      }
    },
    [navigate]
  );

  const establishSession = useCallback(async (authResponse) => {
    if (!authResponse.token) {
      throw new Error('The server did not return an access token.');
    }

    void queryClient.cancelQueries();
    queryClient.clear();
    localStorage.setItem(TOKEN_STORAGE_KEY, authResponse.token);

    try {
      const profile = await getCurrentUser();
      setUser(profile);
      return profile;
    } catch (error) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      throw error;
    }
  }, []);

  const login = useCallback(
    async (credentials) => establishSession(await loginUser(credentials)),
    [establishSession]
  );

  const register = useCallback(
    async (profile) => establishSession(await registerUser(profile)),
    [establishSession]
  );

  useEffect(() => {
    const token = localStorage.getItem(TOKEN_STORAGE_KEY);

    if (!token) {
      setIsLoading(false);
      return;
    }

    let active = true;

    getCurrentUser()
      .then((profile) => {
        if (active) {
          setUser(profile);
        }
      })
      .catch(() => {
        if (active && localStorage.getItem(TOKEN_STORAGE_KEY) === token) {
          localStorage.removeItem(TOKEN_STORAGE_KEY);
          setUser(null);
        }
      })
      .finally(() => {
        if (active) {
          setIsLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    const handleUnauthorized = () => logout();
    const handleStorage = (event) => {
      if (event.key === TOKEN_STORAGE_KEY && event.oldValue && !event.newValue) {
        logout();
      }
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    window.addEventListener('storage', handleStorage);

    return () => {
      window.removeEventListener('auth:unauthorized', handleUnauthorized);
      window.removeEventListener('storage', handleStorage);
    };
  }, [logout]);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isLoading,
      login,
      register,
      logout,
    }),
    [isLoading, login, logout, register, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }

  return context;
}
