import { useState } from 'react';
import { Alert, Button, Form, Input, message } from 'antd';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/useAuth';
import type { LoginCredentials } from '../../types/auth';

export default function LoginPage() {
  const { login } = useAuth(); const navigate = useNavigate(); const location = useLocation(); const [loading, setLoading] = useState(false);
  const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname;
  const onFinish = async (values: LoginCredentials) => { setLoading(true); try { const user = await login(values); message.success('Đăng nhập thành công'); navigate(from || (user.role === 'ADMIN' ? '/admin/dashboard' : '/'), { replace: true }); } catch (error) { message.error(error instanceof Error ? error.message : 'Không thể đăng nhập'); } finally { setLoading(false); } };
  return <div className="auth-wrap"><div className="auth-card"><div className="mb-7 text-center"><Link to="/" className="brand">Chợ 2hand</Link><h1 className="mb-1 mt-5 text-2xl font-bold text-slate-800">Chào mừng trở lại</h1><p className="text-slate-500">Đăng nhập để tiếp tục mua bán cùng cộng đồng sinh viên.</p></div>
    <Form layout="vertical" size="large" onFinish={onFinish}><Form.Item name="email" label="Email" rules={[{ required: true, message: 'Vui lòng nhập email' }, { type: 'email', message: 'Email không hợp lệ' }]}><Input prefix={<MailOutlined />} placeholder="name@example.com" /></Form.Item><Form.Item name="password" label="Mật khẩu" rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}><Input.Password prefix={<LockOutlined />} placeholder="Nhập mật khẩu" /></Form.Item><Button type="primary" htmlType="submit" block loading={loading}>Đăng nhập</Button></Form>
    <Alert className="mt-5" type="info" showIcon message="Tài khoản demo" description={<div className="space-y-1 text-xs"><div><b>USER:</b> user@cho2hand.local / User123!</div><div><b>ADMIN:</b> admin@cho2hand.local / Admin123!</div><div><b>Backend sample USER:</b> demo@marketplace.local / password</div></div>} />
    <p className="mb-0 mt-5 text-center text-sm text-slate-500">Chưa có tài khoản? <Link className="text-blue-600" to="/register">Đăng ký ngay</Link></p>
  </div></div>;
}
