import { useState } from "react";
import { Button, Typography, message } from "antd";
import axiosClient from "../api/axiosClient";

export default function Home() {
  const [loading, setLoading] = useState(false);

  // Nút kiểm tra tạm: gọi thử BE để chắc axios chạy. Xóa khi làm trang chủ thật (UC03).
  const checkApi = async () => {
    setLoading(true);
    try {
      const res = await axiosClient.get("/categories");
      message.success(`Kết nối BE thành công (${JSON.stringify(res).slice(0, 80)}...)`);
    } catch (err) {
      message.error(`Lỗi: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Typography.Title level={3}>Trang chủ</Typography.Title>
      <Typography.Paragraph type="secondary">
        Danh sách sản phẩm sẽ làm ở Sprint 1 (UC03).
      </Typography.Paragraph>
      <Button onClick={checkApi} loading={loading}>
        Kiểm tra kết nối API (tạm)
      </Button>
    </div>
  );
}
