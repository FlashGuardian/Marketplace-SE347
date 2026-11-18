import { Button, Input, Space } from 'antd';
import { BellOutlined, SearchOutlined, ShoppingCartOutlined, UserOutlined } from '@ant-design/icons';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/useAuth';

export default function MainLayout() {
  const { user, isAuthenticated, logout } = useAuth(); const navigate = useNavigate();
  return <div className="app-shell">
    <header className="site-header"><div className="site-header-inner">
      <div className="flex flex-wrap items-center gap-5"><Link to="/" className="brand">Chợ 2hand</Link><div className="min-w-[220px] flex-1"><Input size="large" placeholder="Tìm sản phẩm..." prefix={<SearchOutlined />} onPressEnter={(e) => navigate(`/search?q=${encodeURIComponent(e.currentTarget.value)}`)} /></div><Space size={12}>
        {user?.role !== 'ADMIN' && <Button type="text" icon={<ShoppingCartOutlined />} onClick={() => navigate('/cart')} aria-label="Giỏ hàng" />}
        {user?.role !== 'ADMIN' && <Button type="text" icon={<BellOutlined />} onClick={() => navigate(isAuthenticated ? '/notifications' : '/login')} aria-label="Thông báo" />}
        {isAuthenticated ? <><Button type="text" icon={<UserOutlined />} onClick={() => navigate('/profile')}>{user?.displayName}</Button><Button onClick={logout}>Đăng xuất</Button>{user?.role !== 'ADMIN' && <Button type="primary" onClick={() => navigate('/sell')}>+ Đăng bán</Button>}</> : <><Button onClick={() => navigate('/login')}>Đăng nhập</Button><Button type="primary" onClick={() => navigate('/register')}>Đăng ký</Button></>}
      </Space></div>
      <nav className="top-nav"><NavLink to="/" end>Khám phá</NavLink>{isAuthenticated && <><NavLink to="/my-products">Sản phẩm của tôi</NavLink><NavLink to="/my-orders">Đơn hàng của tôi</NavLink><NavLink to="/chat">Tin nhắn</NavLink></>}<span className="ml-auto hidden text-xs text-slate-400 sm:block">Trao đổi cũ • Đón điều mới</span></nav>
    </div></header>
    <main className="page-main"><Outlet /></main>
    <footer className="site-footer"><div className="footer-inner"><div><div className="brand">Chợ 2hand</div><p className="mb-1 mt-2">Cũ với bạn, mới với ai đó.</p><small>© 2026 Chợ 2hand</small></div><div><b className="text-slate-700">Chợ đồ cũ của cộng đồng sinh viên</b><p className="mb-0 mt-2">Mua bán gần nhau • Tiết kiệm hơn • Sống xanh hơn</p></div><div><b className="text-slate-700">Hỗ trợ & An toàn</b><p className="mb-0 mt-2">Liên hệ: hotro@cho2hand.vn</p></div></div></footer>
  </div>;
}
