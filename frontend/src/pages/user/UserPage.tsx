import type { ReactNode } from 'react';
import { useLocation } from 'react-router-dom';
import { ShoppingCartOutlined, ShoppingOutlined, InboxOutlined, FileTextOutlined, MessageOutlined, BellOutlined, UserOutlined, LockOutlined } from '@ant-design/icons';
import PagePlaceholder from '../../components/common/PagePlaceholder';
const pages: Record<string,{title:string;description:string;icon: ReactNode}> = {
  '/cart': { title:'Giỏ hàng', description:'UC09 · Quản lý các sản phẩm đã chọn trước khi tạo đơn hàng.', icon:<ShoppingCartOutlined /> },
  '/checkout': { title:'Đặt hàng', description:'UC10 · Xác nhận sản phẩm và thông tin cần thiết để tạo đơn hàng.', icon:<FileTextOutlined /> },
  '/sell': { title:'Đăng bán sản phẩm', description:'UC06 · Đăng tải sản phẩm đã qua sử dụng.', icon:<ShoppingOutlined /> },
  '/my-products': { title:'Sản phẩm của tôi', description:'UC07–UC08 · Chỉnh sửa, ẩn hoặc hiện lại sản phẩm thuộc quyền sở hữu của bạn.', icon:<InboxOutlined /> },
  '/my-orders': { title:'Đơn hàng của tôi', description:'UC11–UC14 · Theo dõi đơn mua và đơn bán; thao tác theo đúng vai trò trong từng giao dịch.', icon:<FileTextOutlined /> },
  '/chat': { title:'Tin nhắn', description:'UC15 · Trao đổi giữa người mua và người bán.', icon:<MessageOutlined /> },
  '/notifications': { title:'Thông báo', description:'Thông báo trong hệ thống về đơn hàng, tin nhắn và hoạt động liên quan.', icon:<BellOutlined /> },
  '/profile': { title:'Hồ sơ cá nhân', description:'UC23 · Cập nhật thông tin cá nhân của tài khoản.', icon:<UserOutlined /> },
  '/change-password': { title:'Đổi mật khẩu', description:'Khu vực giao diện dự kiến cho thao tác đổi mật khẩu.', icon:<LockOutlined /> },
};
export default function UserPage() { const { pathname } = useLocation(); const page=pages[pathname] ?? pages['/profile']; return <PagePlaceholder {...page} />; }
