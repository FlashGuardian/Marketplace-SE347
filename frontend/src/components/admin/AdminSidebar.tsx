import { BarChartOutlined, FileSearchOutlined, FolderOutlined, FlagOutlined, TeamOutlined } from '@ant-design/icons';
import { NavLink } from 'react-router-dom';

const items = [
  { to: '/admin/statistics', label: 'Thống kê', icon: <BarChartOutlined /> },
  { to: '/admin/accounts', label: 'Tài khoản', icon: <TeamOutlined /> },
  { to: '/admin/categories', label: 'Danh mục', icon: <FolderOutlined /> },
  { to: '/admin/products', label: 'Kiểm duyệt sản phẩm', icon: <FileSearchOutlined /> },
  { to: '/admin/reports', label: 'Báo cáo vi phạm', icon: <FlagOutlined /> },
];
export default function AdminSidebar() { return <aside className="admin-sidebar"><div className="mb-7 px-2 text-[11px] font-semibold tracking-wide text-slate-400">KHÔNG GIAN QUẢN TRỊ</div><nav className="admin-menu">{items.map(item => <NavLink key={item.to} to={item.to} className={({ isActive }) => `admin-menu-link ${isActive ? 'active' : ''}`}><span className="text-base">{item.icon}</span><span>{item.label}</span></NavLink>)}</nav><div className="mt-auto px-2 pt-8"><div className="text-xl font-bold text-blue-600">Chợ 2hand</div><p className="mt-2 text-sm text-slate-400">Vì một cộng đồng tin cậy.</p></div></aside>; }
