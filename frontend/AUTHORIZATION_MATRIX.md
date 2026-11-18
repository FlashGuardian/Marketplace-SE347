# Ma trận route Frontend (đối chiếu SRS / Use Case)

| Nhóm route | Guest | USER | ADMIN | Ghi chú |
|---|---:|---:|---:|---|
| `/`, `/search`, `/products/:productId` | Cho phép | Cho phép | Cho phép | SRS ma trận chức năng 3–5; G-01 cho guest |
| `/login`, `/register` | Cho phép | Có thể mở | Có thể mở | Form đăng ký chưa kết nối API |
| `/cart`, `/checkout` | Chặn | Cho phép | Chặn | UC09–UC10 |
| `/sell`, `/my-products` | Chặn | Cho phép | Chặn | UC06–UC08; USER có cả ngữ cảnh mua và bán |
| `/my-orders` | Chặn | Cho phép | Chặn | UC11–UC14; chỉ dữ liệu đơn của chính người dùng |
| `/chat` | Chặn | Cho phép | Chặn | UC15; chỉ hội thoại thuộc người dùng |
| `/notifications` | Chặn | Cho phép | Chặn | Thông báo in-app (G-10) |
| `/profile`, `/change-password` | Chặn | Cho phép | Chặn | UC23 / màn hình tài khoản |
| `/admin/dashboard`, `/admin/statistics` | Chặn | Chặn | Cho phép | UC22; thống kê tổng quan |
| `/admin/accounts` | Chặn | Chặn | Cho phép | UC18; khóa/mở khóa có lý do và nhật ký |
| `/admin/categories` | Chặn | Chặn | Cho phép | UC19; danh mục đang có sản phẩm chỉ vô hiệu hóa (BR-10) |
| `/admin/products`, `/admin/reports` | Chặn | Chặn | Cho phép | UC17–UC21; kiểm duyệt và xử lý báo cáo |
| `/admin/reports/:reportId/orders/:orderId` | Chặn | Chặn | Cho phép có điều kiện | UC11 / UC21; chỉ mở từ ngữ cảnh báo cáo, chỉ đọc; cần Backend ghi nhật ký |

## Diễn giải role
- `USER` là một tài khoản người dùng duy nhất có thể mua và bán tùy theo ngữ cảnh nghiệp vụ (SRS mục 2.3; Use Case giả định G-02).
- `ADMIN` là role quản trị độc lập.
- `ProtectedRoute` bảo vệ khu vực cần đăng nhập và hỗ trợ `allowedRoles`.
- `AdminRoute` chỉ cho phép role `ADMIN` vào `/admin/*`.

## Giới hạn bảo mật
Đây là route guard phía trình duyệt kết hợp mock session trong `sessionStorage`. Người dùng có thể chỉnh sửa dữ liệu lưu trong trình duyệt. Backend hiện chưa cấp JWT/role và chưa có Spring Security authorization; do đó không được dùng cơ chế này để bảo vệ API hoặc dữ liệu thật. Khi triển khai Backend Auth, cần xác thực role ở Backend và thay `mockAuthService` bằng adapter gọi API.
