import { Descriptions, Result, Tag } from 'antd';
import { ReadOutlined } from '@ant-design/icons';
import { Navigate, useLocation } from 'react-router-dom';

export default function AdminRelatedOrderPage() {
  const location = useLocation();
  const openedFromReport = (location.state as { fromReport?: boolean } | null)?.fromReport === true;
  if (!openedFromReport) return <Navigate to="/admin/reports" replace />;

  return <div className="space-y-5">
    <div><Tag color="warning">CHỈ ĐỌC · TRUY CẬP THEO BÁO CÁO</Tag><h1 className="mt-2 text-2xl font-bold text-slate-800">Đơn hàng liên quan</h1><p className="text-slate-500">UC11 / UC21 · Admin chỉ được xem đơn hàng liên quan khi xử lý báo cáo; không có thao tác chỉnh sửa.</p></div>
    <Result icon={<ReadOutlined className="text-blue-500" />} title="Khung tra cứu đơn hàng chỉ đọc" subTitle="Dữ liệu và nhật ký truy cập sẽ được kết nối khi Backend hỗ trợ phân quyền và ghi log." />
    <Descriptions bordered size="small" column={1}><Descriptions.Item label="Quyền thao tác">Chỉ xem</Descriptions.Item><Descriptions.Item label="Điều kiện">Điều hướng từ một báo cáo vi phạm liên quan</Descriptions.Item><Descriptions.Item label="Ghi nhật ký">Bắt buộc theo BR-11 và BR-12 khi triển khai Backend</Descriptions.Item></Descriptions>
  </div>;
}
