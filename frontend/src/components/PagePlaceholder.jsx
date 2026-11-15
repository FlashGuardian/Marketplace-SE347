import { Typography } from "antd";

// Dùng tạm cho các trang chưa làm. Khi làm trang thật thì thay bằng nội dung.
export default function PagePlaceholder({ title }) {
  return (
    <div>
      <Typography.Title level={3}>{title}</Typography.Title>
      <Typography.Text type="secondary">Trang này sẽ được hoàn thiện ở các sprint sau.</Typography.Text>
    </div>
  );
}
