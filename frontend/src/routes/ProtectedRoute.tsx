import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { Spin } from 'antd';
import { useAuth } from '../contexts/useAuth';
import type { UserRole } from '../types/auth';

interface ProtectedRouteProps { allowedRoles?: UserRole[]; }

export default function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const { isAuthenticated, isInitializing, user } = useAuth();
  const location = useLocation();
  if (isInitializing) return <div className="grid min-h-[50vh] place-items-center"><Spin size="large" tip="Đang kiểm tra phiên đăng nhập..." /></div>;
  if (!isAuthenticated) return <Navigate to="/login" replace state={{ from: location }} />;
  if (allowedRoles && user && !allowedRoles.includes(user.role)) return <Navigate to="/403" replace />;
  return <Outlet />;
}
