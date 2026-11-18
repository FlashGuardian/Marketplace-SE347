import { Layout, Input, Button, Space } from "antd";
import { ShoppingCartOutlined } from "@ant-design/icons";
import { Link, Outlet, useNavigate } from "react-router-dom";

const { Header, Content, Footer } = Layout;

export default function MainLayout() {
  const navigate = useNavigate();

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Header style={{ display: "flex", alignItems: "center", gap: 16 }}>
        <Link to="/" style={{ color: "#fff", fontSize: 30, fontWeight: 600, whiteSpace: "nowrap" }}>
          Chợ 2hand
        </Link>
              <Input.Search
          size="large"
          placeholder="Tìm sản phẩm..."
          allowClear
          style={{ maxWidth: 420 }}
          onSearch={(value) => navigate(`/?q=${encodeURIComponent(value)}`)}
        />
        <Space style={{ marginLeft: "auto" }}>
          <Button type="text" style={{ color: "#fff" }} icon={<ShoppingCartOutlined />} />
          <Button size="large" onClick={() => navigate("/login")}>Đăng nhập</Button>
          <Button size="large" type="primary" onClick={() => navigate("/register")}>
            Đăng ký
          </Button>
        </Space>
      </Header>
      <Content style={{ padding: 24, maxWidth: 1200, width: "100%", margin: "0 auto" }}>
        <Outlet />
      </Content>
      <Footer style={{ textAlign: "center" }}>Chợ 2hand</Footer>
    </Layout>
  );
}
