import type { ReactNode } from 'react';
import { useLocation } from 'react-router-dom';
import { BarChartOutlined, TeamOutlined, FolderOutlined, FileSearchOutlined, FlagOutlined } from '@ant-design/icons';
import PagePlaceholder from '../../components/common/PagePlaceholder';
const pages: Record<string,{title:string;description:string;icon: ReactNode}> = {
 '/admin/dashboard': {title:'Thống kê quản trị',description:'UC22 · Số liệu tổng quan người dùng, sản phẩm, đơn hàng và báo cáo.',icon:<BarChartOutlined />},
 '/admin/accounts': {title:'Quản lý tài khoản',description:'UC18 · Xem danh sách, tìm kiếm, khóa hoặc mở khóa tài khoản theo chính sách.',icon:<TeamOutlined />},
 '/admin/categories': {title:'Quản lý danh mục',description:'UC19 · Thêm, sửa hoặc vô hiệu hóa danh mục; danh mục đang có sản phẩm không được xóa.',icon:<FolderOutlined />},
 '/admin/products': {title:'Kiểm duyệt sản phẩm',description:'UC20–UC21 · Rà soát, ẩn hoặc gỡ sản phẩm vi phạm và ghi nhận lý do xử lý.',icon:<FileSearchOutlined />},
 '/admin/reports': {title:'Báo cáo vi phạm',description:'UC17–UC21 · Tiếp nhận, xác minh và cập nhật kết quả xử lý báo cáo.',icon:<FlagOutlined />},
 '/admin/statistics': {title:'Thống kê quản trị',description:'UC22 · Tổng hợp số liệu người dùng, sản phẩm, đơn hàng và báo cáo theo khoảng thời gian.',icon:<BarChartOutlined />},
};
export default function AdminPage() { const {pathname}=useLocation(); return <PagePlaceholder {...(pages[pathname] ?? pages['/admin/dashboard'])} />; }
