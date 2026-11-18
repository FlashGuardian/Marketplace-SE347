import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { Spin } from 'antd';
import { useAuth } from '../contexts/useAuth';

export default function AdminRoute() {
  const { isAuthenticated, isInitializing, user } = useAuth();
  const location = useLocation();
  if (isInitializing) return <div className="grid min-h-[50vh] place-items-center"><Spin size="large" tip="Đang kiểm tra quyền truy cập..." /></div>;
  if (!isAuthenticated) return <Navigate to="/login" replace state={{ from: location }} />;
  if (user?.role !== 'ADMIN') return <Navigate to="/403" replace />;
  return <Outlet />;
}
