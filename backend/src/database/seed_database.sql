USE secondhand_market;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE audit_logs;
TRUNCATE TABLE notifications;
TRUNCATE TABLE reports;
TRUNCATE TABLE reviews;
TRUNCATE TABLE messages;
TRUNCATE TABLE conversations;
TRUNCATE TABLE order_status_history;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE product_images;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- Users
-- All test accounts use password: 12345678
-- ------------------------------------------------------------
INSERT INTO users (
  id, full_name, email, phone, password_hash, avatar_url,
  default_address, role, status, rating_avg, rating_count
) VALUES
  (1, 'Demo User', 'demo@marketplace.local', '0900000001',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   NULL, 'Khu phố 1, TP. Thủ Đức', 'USER', 'ACTIVE', 4.80, 2),
  (1001, 'Nguyễn Minh Anh', 'user@cho2hand.local', '0900000002',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   NULL, 'Ký túc xá khu A, Dĩ An', 'USER', 'ACTIVE', 4.50, 2),
  (1002, 'Quản trị viên', 'admin@cho2hand.local', '0900000003',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   NULL, NULL, 'ADMIN', 'ACTIVE', 5.00, 0),
  (1003, 'Trần Thị Bình', 'binh@cho2hand.local', '0900000004',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   NULL, 'Quận 1, TP. Hồ Chí Minh', 'USER', 'ACTIVE', 4.70, 3),
  (1004, 'Lê Quốc Cường', 'cuong@cho2hand.local', '0900000005',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   NULL, 'Ký túc xá khu B, Dĩ An', 'USER', 'LOCKED', 3.00, 1);

-- ------------------------------------------------------------
-- Categories
-- ------------------------------------------------------------
INSERT INTO categories (id, name, description, is_active) VALUES
  (1, 'Sách - Giáo trình', 'Sách, giáo trình, tài liệu học tập', TRUE),
  (2, 'Đồ điện tử', 'Laptop, điện thoại, phụ kiện', TRUE),
  (3, 'Đồ gia dụng', 'Đồ dùng trong ký túc xá, phòng trọ', TRUE),
  (4, 'Quần áo - Phụ kiện', 'Thời trang đã qua sử dụng', TRUE),
  (5, 'Xe cộ', 'Xe đạp, xe máy, phụ kiện', TRUE),
  (6, 'Khác', 'Các sản phẩm khác', TRUE);

-- ------------------------------------------------------------
-- Products
-- ------------------------------------------------------------
INSERT INTO products (
  id, seller_id, category_id, name, description, price,
  condition_level, pickup_location, status
) VALUES
  (1, 1001, 1, 'Giáo trình Cấu trúc dữ liệu',
   'Giáo trình năm 2, có ghi chú nhẹ và còn đầy đủ trang.',
   85000, 'GOOD', 'Ký túc xá khu A', 'SELLING'),
  (2, 1001, 2, 'Chuột không dây Logitech',
   'Chuột không dây, pin mới, kết nối ổn định.',
   180000, 'LIKE_NEW', 'Thủ Đức', 'SELLING'),
  (3, 1003, 2, 'Tai nghe Bluetooth',
   'Tai nghe dùng 6 tháng, kèm hộp sạc và dây USB-C.',
   320000, 'GOOD', 'Quận 1', 'HELD'),
  (4, 1003, 3, 'Đèn bàn học',
   'Đèn LED có thể điều chỉnh độ sáng, bảo hành 1 tháng.',
   150000, 'GOOD', 'Quận 10', 'SELLING'),
  (5, 1001, 4, 'Áo khoác hoodie',
   'Áo hoodie size M, ít sử dụng, không phai màu.',
   120000, 'LIKE_NEW', 'Dĩ An', 'SOLD'),
  (6, 1004, 5, 'Xe đạp thể thao',
   'Xe đạp 26 inch, cần bảo dưỡng phanh trước.',
   900000, 'FAIR', 'Ký túc xá khu B', 'HIDDEN');

INSERT INTO product_images (id, product_id, image_url, sort_order, is_thumbnail) VALUES
  (1, 1, '/images/products/data-structures.jpg', 1, TRUE),
  (2, 2, '/images/products/logitech-mouse.jpg', 1, TRUE),
  (3, 3, '/images/products/bluetooth-headphone.jpg', 1, TRUE),
  (4, 4, '/images/products/desk-lamp.jpg', 1, TRUE),
  (5, 5, '/images/products/hoodie.jpg', 1, TRUE),
  (6, 6, '/images/products/bicycle.jpg', 1, TRUE);

-- ------------------------------------------------------------
-- Cart
-- ------------------------------------------------------------
INSERT INTO cart_items (id, user_id, product_id, is_selected) VALUES
  (1, 1, 1, TRUE),
  (2, 1, 2, TRUE),
  (3, 1, 4, FALSE);

-- ------------------------------------------------------------
-- Orders
-- ------------------------------------------------------------
INSERT INTO orders (
  id, order_code, buyer_id, seller_id, status, receiver_name,
  receiver_phone, receiver_address, delivery_method, note,
  total_amount, expires_at, confirmed_at, completed_at
) VALUES
  (1, 'DH100001', 1, 1001, 'COMPLETED',
   'Demo User', '0900000001', 'Khu phố 1, TP. Thủ Đức',
   'MEET', 'Giao vào cuối tuần', 120000,
   '2026-11-20 12:00:00', '2026-11-18 13:00:00', '2026-11-19 10:00:00'),
  (2, 'DH100002', 1, 1003, 'PENDING',
   'Demo User', '0900000001', 'Khu phố 1, TP. Thủ Đức',
   'SELLER_DELIVERS', NULL, 320000,
   '2026-11-21 12:00:00', NULL, NULL),
  (3, 'DH100003', 1001, 1003, 'CONFIRMED',
   'Nguyễn Minh Anh', '0900000002', 'Ký túc xá khu A',
   'MEET', 'Liên hệ trước khi tới', 150000,
   '2026-11-22 12:00:00', '2026-11-20 09:00:00', NULL);

INSERT INTO order_items (id, order_id, product_id, product_name, unit_price) VALUES
  (1, 1, 5, 'Áo khoác hoodie', 120000),
  (2, 2, 3, 'Tai nghe Bluetooth', 320000),
  (3, 3, 4, 'Đèn bàn học', 150000);

INSERT INTO order_status_history (
  id, order_id, from_status, to_status, changed_by, reason
) VALUES
  (1, 1, NULL, 'PENDING', NULL, 'Người mua tạo đơn'),
  (2, 1, 'PENDING', 'CONFIRMED', 1001, 'Người bán xác nhận'),
  (3, 1, 'CONFIRMED', 'COMPLETED', 1, 'Đã nhận hàng'),
  (4, 2, NULL, 'PENDING', NULL, 'Người mua tạo đơn'),
  (5, 3, NULL, 'PENDING', NULL, 'Người mua tạo đơn'),
  (6, 3, 'PENDING', 'CONFIRMED', 1003, 'Người bán xác nhận');

-- ------------------------------------------------------------
-- Conversations and messages
-- ------------------------------------------------------------
INSERT INTO conversations (id, user1_id, user2_id, last_message_at) VALUES
  (1, 1, 1001, '2026-11-19 10:20:00'),
  (2, 1, 1003, '2026-11-19 11:00:00');

INSERT INTO messages (
  id, conversation_id, sender_id, content, product_id, order_id, is_read
) VALUES
  (1, 1, 1, 'Giáo trình này còn không?', 1, NULL, TRUE),
  (2, 1, 1001, 'Còn nhé, bạn có thể xem hôm nay.', 1, NULL, TRUE),
  (3, 1, 1, 'Được, mình sẽ ghé ký túc xá.', 1, NULL, FALSE),
  (4, 2, 1, 'Tai nghe còn bảo hành không?', 3, 2, TRUE),
  (5, 2, 1003, 'Còn bảo hành cửa hàng 1 tháng.', 3, 2, FALSE);

-- ------------------------------------------------------------
-- Reviews
-- ------------------------------------------------------------
INSERT INTO reviews (id, order_id, reviewer_id, reviewee_id, rating, comment) VALUES
  (1, 1, 1, 1001, 5, 'Người bán thân thiện, hàng đúng mô tả'),
  (2, 1, 1001, 1, 4, 'Người mua thanh toán nhanh');

-- ------------------------------------------------------------
-- Reports
-- ------------------------------------------------------------
INSERT INTO reports (
  id, reporter_id, target_type, product_id, reported_user_id,
  reason, description, status, result, action_taken, handled_by,
  handler_note
) VALUES
  (1, 1, 'PRODUCT', 6, NULL, 'MISDESCRIBED',
   'Sản phẩm không đúng tình trạng được mô tả.',
   'PENDING', NULL, NULL, NULL, NULL),
  (2, 1001, 'USER', NULL, 1004, 'SCAM',
   'Tài khoản không giao hàng sau khi nhận thanh toán.',
   'RESOLVED', 'VALID', 'LOCK_ACCOUNT', 1002,
   'Đã xác minh và khóa tài khoản.');

-- ------------------------------------------------------------
-- Notifications
-- ------------------------------------------------------------
INSERT INTO notifications (
  id, user_id, type, title, content, ref_type, ref_id, is_read
) VALUES
  (1, 1, 'ORDER_CONFIRMED', 'Đơn hàng đã được xác nhận',
   'Người bán đã xác nhận đơn DH100002.', 'ORDER', 2, FALSE),
  (2, 1001, 'NEW_MESSAGE', 'Tin nhắn mới',
   'Bạn có tin nhắn mới từ Demo User.', 'CONVERSATION', 1, FALSE),
  (3, 1004, 'ACCOUNT_LOCKED', 'Tài khoản đã bị khóa',
   'Tài khoản của bạn đã bị khóa do vi phạm.', NULL, NULL, TRUE),
  (4, 1003, 'ORDER_CREATED', 'Có đơn hàng mới',
   'Bạn vừa nhận đơn hàng DH100003.', 'ORDER', 3, TRUE);

-- ------------------------------------------------------------
-- Audit logs
-- ------------------------------------------------------------
INSERT INTO audit_logs (
  id, admin_id, action, target_type, target_id, reason
) VALUES
  (1, 1002, 'LOCK_USER', 'USER', 1004,
   'Xử lý báo cáo hành vi gian lận'),
  (2, 1002, 'HIDE_PRODUCT', 'PRODUCT', 6,
   'Sản phẩm có tình trạng không đúng mô tả'),
  (3, 1002, 'HANDLE_REPORT', 'REPORT', 2,
   'Báo cáo hợp lệ, đã khóa tài khoản vi phạm');
