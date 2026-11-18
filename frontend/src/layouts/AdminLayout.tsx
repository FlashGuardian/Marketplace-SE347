import { Outlet } from 'react-router-dom';
import AdminSidebar from '../components/admin/AdminSidebar';
import AdminHeader from '../components/admin/AdminHeader';
export default function AdminLayout() { return <div className="admin-shell"><AdminHeader /><div className="admin-body"><AdminSidebar /><main className="admin-content"><Outlet /></main></div><footer className="border-t border-slate-100 bg-white px-6 py-4 text-center text-xs text-slate-400">© 2026 Chợ 2hand · Khu vực quản trị</footer></div>; }
