# Marketplace / Chợ 2hand – Frontend starter

Frontend được khởi tạo theo phương án đã duyệt: React + TypeScript + Vite, Ant Design và Tailwind CSS.

## Yêu cầu
- Node.js 20.19+ hoặc 22.12+
- npm 10+

## Chạy ứng dụng
```bash
cd frontend
npm install
npm run dev
```
Mở `http://localhost:5173`.

## Tài khoản demo (Frontend-only)
| Role | Email | Password |
|---|---|---|
| USER | `user@cho2hand.local` | `User123!` |
| ADMIN | `admin@cho2hand.local` | `Admin123!` |
| USER (backend sample) | `demo@marketplace.local` | `password` |

Phiên demo được lưu trong `sessionStorage`, tự mất khi đóng tab/session. Đây chỉ là mô phỏng luồng giao diện, không phải xác thực an toàn.

## Route chính
- Public: `/`, `/search`, `/products/:productId`, `/login`, `/register`
- User đã đăng nhập: `/cart`, `/checkout`, `/sell`, `/my-products`, `/my-orders`, `/chat`, `/notifications`, `/profile`, `/change-password`
- Admin: `/admin/statistics`, `/admin/accounts`, `/admin/categories`, `/admin/products`, `/admin/reports`
- Admin chỉ đọc đơn liên quan: `/admin/reports/:reportId/orders/:orderId` (yêu cầu điều hướng với `state: { fromReport: true }`)
- Lỗi: `/403`, route không tồn tại → `/404`-style page

## Lưu ý Authentication
Backend trong source hiện tại chỉ có `POST /api/auth/login`, response gồm `userId`, `email`, `displayName`, `message`; chưa có JWT hoặc Role. Vì phạm vi đã duyệt là Frontend-only, `authService.ts` đang dùng `mockAuthService.ts`. Khi Backend bổ sung xác thực/role, thay adapter này bằng API thật và để Backend bảo vệ các API riêng tư. Không dùng dữ liệu `sessionStorage` làm cơ chế bảo mật.

## Phạm vi hiện tại
Các page chức năng đã có route và page skeleton để nhóm tiếp tục phát triển. Chưa triển khai CRUD, dữ liệu giao dịch, API nghiệp vụ, upload ảnh, chat realtime hoặc biểu đồ thống kê thực.
