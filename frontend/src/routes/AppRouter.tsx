import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import AdminLayout from '../layouts/AdminLayout';
import ProtectedRoute from './ProtectedRoute';
import AdminRoute from './AdminRoute';
import HomePage from '../pages/public/HomePage';
import SearchPage from '../pages/public/SearchPage';
import ProductDetailPage from '../pages/public/ProductDetailPage';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import UserPage from '../pages/user/UserPage';
import AdminPage from '../pages/admin/AdminPage';
import AdminRelatedOrderPage from '../pages/admin/AdminRelatedOrderPage';
import ForbiddenPage from '../pages/errors/ForbiddenPage';
import NotFoundPage from '../pages/errors/NotFoundPage';
import CartPage from '../pages/cart/CartPage';

export default function AppRouter() { return <BrowserRouter><Routes>
  <Route element={<MainLayout />}>
    <Route index element={<HomePage />} /><Route path="search" element={<SearchPage />} /><Route path="products/:productId" element={<ProductDetailPage />} />
    <Route path="login" element={<LoginPage />} /><Route path="register" element={<RegisterPage />} />
    <Route element={<ProtectedRoute allowedRoles={['USER']} />}>
      <Route path="cart" element={<CartPage />} /><Route path="checkout" element={<UserPage />} /><Route path="sell" element={<UserPage />} /><Route path="my-products" element={<UserPage />} /><Route path="my-orders" element={<UserPage />} /><Route path="chat" element={<UserPage />} /><Route path="notifications" element={<UserPage />} /><Route path="profile" element={<UserPage />} /><Route path="change-password" element={<UserPage />} />
    </Route>
    <Route path="403" element={<ForbiddenPage />} />
  </Route>
  <Route element={<AdminRoute />}><Route path="admin" element={<AdminLayout />}>
    <Route index element={<Navigate to="statistics" replace />} /><Route path="dashboard" element={<Navigate to="statistics" replace />} /><Route path="accounts" element={<AdminPage />} /><Route path="categories" element={<AdminPage />} /><Route path="products" element={<AdminPage />} /><Route path="reports" element={<AdminPage />} /><Route path="statistics" element={<AdminPage />} /><Route path="reports/:reportId/orders/:orderId" element={<AdminRelatedOrderPage />} />
  </Route></Route>
  <Route path="*" element={<NotFoundPage />} />
</Routes></BrowserRouter>; }
