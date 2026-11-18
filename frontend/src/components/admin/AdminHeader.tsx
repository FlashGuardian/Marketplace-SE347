import { Avatar, Button, Dropdown, Space } from 'antd';
import { LogoutOutlined, UserOutlined } from '@ant-design/icons';
import { useAuth } from '../../contexts/useAuth';
export default function AdminHeader() {
  const { user, logout } = useAuth();
  return <header className="flex min-h-[68px] items-center justify-between border-b border-slate-100 bg-white px-6">
    <div className="font-semibold text-slate-700">Bảng điều khiển quản trị</div>
    <Dropdown menu={{ items: [{ key: 'profile', icon: <UserOutlined />, label: user?.displayName ?? 'Admin', disabled: true }, { type: 'divider' }, { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', danger: true, onClick: logout }] }} trigger={['click']}>
      <Button type="text"><Space><Avatar size="small" style={{ background: '#1677ff' }}>{user?.displayName?.slice(0,1) ?? 'A'}</Avatar><span>{user?.displayName ?? 'Admin'}</span></Space></Button>
    </Dropdown>
  </header>;
}
