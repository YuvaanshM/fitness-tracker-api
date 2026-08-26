import { QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import GuestRoute from './auth/GuestRoute';
import ProtectedRoute from './auth/ProtectedRoute';
import Layout from './components/Layout';
import RouteAccessibility from './components/RouteAccessibility';
import queryClient from './lib/queryClient';
import Dashboard from './pages/Dashboard';
import Food from './pages/Food';
import Home from './pages/Home';
import Insights from './pages/Insights';
import Login from './pages/Login';
import Profile from './pages/Profile';
import Register from './pages/Register';
import Workouts from './pages/Workouts';

export default function App() {
  return (
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <RouteAccessibility />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route element={<GuestRoute />}>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
            </Route>

            <Route element={<ProtectedRoute />}>
              <Route element={<Layout />}>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/workouts" element={<Workouts />} />
                <Route path="/food" element={<Food />} />
                <Route path="/insights" element={<Insights />} />
                <Route path="/profile" element={<Profile />} />
              </Route>
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </AuthProvider>
      </QueryClientProvider>
    </BrowserRouter>
  );
}
