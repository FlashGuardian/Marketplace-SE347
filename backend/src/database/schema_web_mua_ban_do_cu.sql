-- =====================================================================
-- CƠ SỞ DỮ LIỆU: WEB MUA BÁN ĐỒ CŨ DÀNH CHO SINH VIÊN
-- Nguồn: Đặc tả Use Case (UC01-UC23) + Workflow (WF-01..WF-09)
-- DBMS : MySQL 8.0.16+ (InnoDB, utf8mb4) - cần 8.0.16+ để CHECK có hiệu lực
-- =====================================================================

CREATE DATABASE IF NOT EXISTS secondhand_market
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE secondhand_market;

-- ---------------------------------------------------------------------
-- 1. users : tài khoản (người mua + người bán dùng chung, G-02) và Admin
-- ---------------------------------------------------------------------
CREATE TABLE users (
  id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  full_name          VARCHAR(100)  NOT NULL,
  email              VARCHAR(150)  NOT NULL,
  phone              VARCHAR(15)   NOT NULL,
  password_hash      VARCHAR(255)  NOT NULL,                 -- BR: lưu dạng băm
  avatar_url         VARCHAR(500)  NULL,
  default_address    VARCHAR(255)  NULL,
  role               ENUM('USER','ADMIN')      NOT NULL DEFAULT 'USER',
  status             ENUM('ACTIVE','LOCKED')   NOT NULL DEFAULT 'ACTIVE',
  lock_reason        VARCHAR(500)  NULL,                     -- UC18
  rating_avg         DECIMAL(3,2)  NOT NULL DEFAULT 0.00,    -- UC16: điểm TB (denormalize)
  rating_count       INT UNSIGNED  NOT NULL DEFAULT 0,
  failed_login_count TINYINT UNSIGNED NOT NULL DEFAULT 0,    -- UC02 E1: sai 5 lần
  locked_until       DATETIME      NULL,                     -- khóa tạm 15 phút
  created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT uq_users_phone UNIQUE (phone),
  CONSTRAINT ck_users_rating CHECK (rating_avg BETWEEN 0 AND 5)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. categories : danh mục sản phẩm (UC19)
-- ---------------------------------------------------------------------
CREATE TABLE categories (
  id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  description VARCHAR(500) NULL,
  is_active   BOOLEAN      NOT NULL DEFAULT TRUE,            -- BR-10: chỉ vô hiệu hóa, không xóa
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_categories_name UNIQUE (name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. products : sản phẩm đăng bán (UC03-UC08, UC20)
--    Mỗi sản phẩm là 1 món (G-03) => không có cột số lượng
-- ---------------------------------------------------------------------
CREATE TABLE products (
  id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  seller_id        BIGINT UNSIGNED NOT NULL,
  category_id      INT UNSIGNED    NOT NULL,
  name             VARCHAR(200)    NOT NULL,
  description      TEXT            NOT NULL,
  price            DECIMAL(12,0)   NOT NULL,                 -- VND, BR: > 0
  condition_level  ENUM('LIKE_NEW','GOOD','FAIR','POOR') NOT NULL,  -- tình trạng sử dụng
  pickup_location  VARCHAR(255)    NULL,                     -- địa điểm giao nhận
  status           ENUM('SELLING','HELD','SOLD','HIDDEN','REMOVED') NOT NULL DEFAULT 'SELLING',
                   -- SELLING=Đang bán | HELD=Đang giữ | SOLD=Đã bán | HIDDEN=Ẩn | REMOVED=Bị gỡ
  hidden_by        ENUM('SELLER','ADMIN','SYSTEM') NULL,     -- ai ẩn (SYSTEM = do khóa tài khoản, BR-09)
  violation_reason VARCHAR(500)    NULL,                     -- lý do Admin ẩn / gỡ (UC20)
  edited_at        DATETIME        NULL,                     -- UC07: thời điểm chỉnh sửa
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_seller   FOREIGN KEY (seller_id)   REFERENCES users(id),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CONSTRAINT ck_products_price CHECK (price > 0),
  INDEX idx_products_list   (status, category_id, created_at),   -- UC03/UC04
  INDEX idx_products_price  (status, price),
  INDEX idx_products_seller (seller_id, status),                 -- "Sản phẩm của tôi"
  FULLTEXT INDEX ft_products_search (name, description)          -- UC04: tìm từ khóa
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. product_images : tối đa 5 ảnh / sản phẩm, mỗi ảnh <= 5MB (G-11, kiểm ở tầng ứng dụng)
-- ---------------------------------------------------------------------
CREATE TABLE product_images (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  product_id   BIGINT UNSIGNED NOT NULL,
  image_url    VARCHAR(500)    NOT NULL,
  sort_order   TINYINT UNSIGNED NOT NULL DEFAULT 1,
  is_thumbnail BOOLEAN         NOT NULL DEFAULT FALSE,       -- ảnh đại diện
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT uq_images_order UNIQUE (product_id, sort_order),
  CONSTRAINT ck_images_order CHECK (sort_order BETWEEN 1 AND 5)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. cart_items : giỏ hàng (UC09). Mỗi user 1 giỏ => không cần bảng carts riêng
-- ---------------------------------------------------------------------
CREATE TABLE cart_items (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  is_selected BOOLEAN         NOT NULL DEFAULT TRUE,         -- chọn / bỏ chọn
  added_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
  CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  CONSTRAINT uq_cart UNIQUE (user_id, product_id)            -- A1: không thêm trùng
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. orders : đơn hàng (UC10-UC14). Mỗi đơn chỉ của 1 người bán (BR-04)
-- ---------------------------------------------------------------------
CREATE TABLE orders (
  id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_code       VARCHAR(20)     NOT NULL,                 -- mã đơn hiển thị cho người dùng
  buyer_id         BIGINT UNSIGNED NOT NULL,
  seller_id        BIGINT UNSIGNED NOT NULL,
  status           ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','REJECTED','CANCELLED')
                   NOT NULL DEFAULT 'PENDING',
                   -- Chờ xác nhận | Đã xác nhận | Đang giao nhận | Hoàn thành | Đã từ chối | Đã hủy
  receiver_name    VARCHAR(100)    NOT NULL,
  receiver_phone   VARCHAR(15)     NOT NULL,
  receiver_address VARCHAR(255)    NOT NULL,                 -- địa chỉ hoặc điểm hẹn
  delivery_method  ENUM('MEET','SELLER_DELIVERS') NOT NULL,  -- G-05
  note             VARCHAR(500)    NULL,
  total_amount     DECIMAL(12,0)   NOT NULL,
  reject_reason    VARCHAR(500)    NULL,                     -- UC12 A1
  cancel_source    ENUM('BUYER','SELLER','SYSTEM','ADMIN') NULL,  -- SYSTEM = quá 48h; ADMIN = gỡ sản phẩm
  cancel_reason    VARCHAR(500)    NULL,                     -- BR-06
  cancelled_at     DATETIME        NULL,
  expires_at       DATETIME        NOT NULL,                 -- created_at + 48h (BR-05)
  confirmed_at     DATETIME        NULL,
  completed_at     DATETIME        NULL,                     -- mốc tính hạn đánh giá 7 ngày (BR-07)
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_orders_code UNIQUE (order_code),
  CONSTRAINT fk_orders_buyer  FOREIGN KEY (buyer_id)  REFERENCES users(id),
  CONSTRAINT fk_orders_seller FOREIGN KEY (seller_id) REFERENCES users(id),
  CONSTRAINT ck_orders_parties CHECK (buyer_id <> seller_id),          -- BR-03
  INDEX idx_orders_buyer  (buyer_id, status, created_at),              -- tab "Đơn mua"
  INDEX idx_orders_seller (seller_id, status, created_at),             -- tab "Đơn bán"
  INDEX idx_orders_expire (status, expires_at)                         -- job tự hủy quá 48h
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. order_items : sản phẩm trong đơn (lưu snapshot tên + giá tại thời điểm đặt)
-- ---------------------------------------------------------------------
CREATE TABLE order_items (
  id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_id      BIGINT UNSIGNED NOT NULL,
  product_id    BIGINT UNSIGNED NOT NULL,
  product_name  VARCHAR(200)    NOT NULL,                    -- snapshot
  unit_price    DECIMAL(12,0)   NOT NULL,                    -- snapshot (BR-08: giá không đổi khi có đơn)
  CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
  CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT uq_oi UNIQUE (order_id, product_id),
  INDEX idx_oi_product (product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. order_status_history : lịch sử đổi trạng thái đơn (UC11 bước 5, UC12-14)
-- ---------------------------------------------------------------------
CREATE TABLE order_status_history (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_id     BIGINT UNSIGNED NOT NULL,
  from_status  ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','REJECTED','CANCELLED') NULL,
  to_status    ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','REJECTED','CANCELLED') NOT NULL,
  changed_by   BIGINT UNSIGNED NULL,                         -- NULL = hệ thống tự động
  reason       VARCHAR(500)    NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_osh_order FOREIGN KEY (order_id)   REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_osh_user  FOREIGN KEY (changed_by) REFERENCES users(id),
  INDEX idx_osh_order (order_id, created_at)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. conversations : hội thoại giữa 2 người dùng (UC15)
--    Mỗi cặp user chỉ có 1 hội thoại; user1_id < user2_id để tránh trùng (A,B)/(B,A)
-- ---------------------------------------------------------------------
CREATE TABLE conversations (
  id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user1_id        BIGINT UNSIGNED NOT NULL,
  user2_id        BIGINT UNSIGNED NOT NULL,
  last_message_at DATETIME        NULL,
  created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_conv_u1 FOREIGN KEY (user1_id) REFERENCES users(id),
  CONSTRAINT fk_conv_u2 FOREIGN KEY (user2_id) REFERENCES users(id),
  CONSTRAINT ck_conv_order CHECK (user1_id < user2_id),      -- cũng chặn chat với chính mình
  CONSTRAINT uq_conv_pair UNIQUE (user1_id, user2_id),
  INDEX idx_conv_u2 (user2_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. messages : tin nhắn (<= 1000 ký tự, G-11)
--     product_id / order_id: ngữ cảnh tin nhắn (chat từ trang sản phẩm hoặc từ đơn)
-- ---------------------------------------------------------------------
CREATE TABLE messages (
  id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  conversation_id BIGINT UNSIGNED NOT NULL,
  sender_id       BIGINT UNSIGNED NOT NULL,
  content         VARCHAR(1000)   NOT NULL,
  product_id      BIGINT UNSIGNED NULL,
  order_id        BIGINT UNSIGNED NULL,
  is_read         BOOLEAN         NOT NULL DEFAULT FALSE,
  created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_msg_conv    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
  CONSTRAINT fk_msg_sender  FOREIGN KEY (sender_id)       REFERENCES users(id),
  CONSTRAINT fk_msg_product FOREIGN KEY (product_id)      REFERENCES products(id) ON DELETE SET NULL,
  CONSTRAINT fk_msg_order   FOREIGN KEY (order_id)        REFERENCES orders(id)   ON DELETE SET NULL,
  CONSTRAINT ck_msg_content CHECK (CHAR_LENGTH(TRIM(content)) > 0),
  INDEX idx_msg_conv (conversation_id, created_at)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. reviews : đánh giá giao dịch (UC16). Mỗi bên 1 lần / đơn (BR-07)
-- ---------------------------------------------------------------------
CREATE TABLE reviews (
  id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  order_id     BIGINT UNSIGNED NOT NULL,
  reviewer_id  BIGINT UNSIGNED NOT NULL,
  reviewee_id  BIGINT UNSIGNED NOT NULL,
  rating       TINYINT UNSIGNED NOT NULL,
  comment      VARCHAR(500)    NULL,
  created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_rv_order    FOREIGN KEY (order_id)    REFERENCES orders(id),
  CONSTRAINT fk_rv_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
  CONSTRAINT fk_rv_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id),
  CONSTRAINT ck_rv_rating   CHECK (rating BETWEEN 1 AND 5),
  CONSTRAINT ck_rv_self     CHECK (reviewer_id <> reviewee_id),
  CONSTRAINT uq_rv_once UNIQUE (order_id, reviewer_id),
  INDEX idx_rv_reviewee (reviewee_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 12. reports : báo cáo vi phạm (UC17, UC21). Đối tượng là sản phẩm HOẶC tài khoản
-- ---------------------------------------------------------------------
CREATE TABLE reports (
  id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  reporter_id      BIGINT UNSIGNED NOT NULL,
  target_type      ENUM('PRODUCT','USER') NOT NULL,
  product_id       BIGINT UNSIGNED NULL,
  reported_user_id BIGINT UNSIGNED NULL,
  reason           ENUM('PROHIBITED_ITEM','MISDESCRIBED','SCAM','SPAM','OTHER') NOT NULL,
  description      VARCHAR(1000)   NOT NULL,
  status           ENUM('PENDING','RESOLVED') NOT NULL DEFAULT 'PENDING',
  result           ENUM('VALID','REJECTED')   NULL,          -- hợp lệ / bác bỏ
  action_taken     ENUM('NONE','REMOVE_PRODUCT','LOCK_ACCOUNT') NULL,
  handled_by       BIGINT UNSIGNED NULL,
  handler_note     VARCHAR(1000)   NULL,                     -- bắt buộc khi xử lý (UC21 E2)
  handled_at       DATETIME        NULL,
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_rp_reporter FOREIGN KEY (reporter_id)      REFERENCES users(id),
  CONSTRAINT fk_rp_product  FOREIGN KEY (product_id)       REFERENCES products(id),
  CONSTRAINT fk_rp_user     FOREIGN KEY (reported_user_id) REFERENCES users(id),
  CONSTRAINT fk_rp_admin    FOREIGN KEY (handled_by)       REFERENCES users(id),
  CONSTRAINT ck_rp_target CHECK (
    (target_type = 'PRODUCT' AND product_id IS NOT NULL AND reported_user_id IS NULL) OR
    (target_type = 'USER'    AND reported_user_id IS NOT NULL AND product_id IS NULL)
  ),
  INDEX idx_rp_status  (status, created_at),
  INDEX idx_rp_product (product_id),
  INDEX idx_rp_user    (reported_user_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 13. notifications : thông báo trong hệ thống - chuông (G-10)
-- ---------------------------------------------------------------------
CREATE TABLE notifications (
  id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT UNSIGNED NOT NULL,
  type       VARCHAR(40)     NOT NULL,   -- ORDER_CREATED, ORDER_CONFIRMED, ORDER_REJECTED, ORDER_CANCELLED,
                                         -- ORDER_SHIPPING, ORDER_COMPLETED, NEW_MESSAGE, REPORT_HANDLED,
                                         -- PRODUCT_REMOVED, ACCOUNT_LOCKED ...
  title      VARCHAR(150)    NOT NULL,
  content    VARCHAR(500)    NULL,
  ref_type   ENUM('ORDER','PRODUCT','CONVERSATION','REPORT') NULL,   -- đối tượng liên quan
  ref_id     BIGINT UNSIGNED NULL,
  is_read    BOOLEAN         NOT NULL DEFAULT FALSE,
  created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_nt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_nt_user (user_id, is_read, created_at)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 14. audit_logs : nhật ký thao tác quản trị (BR-11, BR-12)
--     Gồm cả việc Admin xem đơn hàng khi xử lý báo cáo (UC11 A2)
-- ---------------------------------------------------------------------
CREATE TABLE audit_logs (
  id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  admin_id    BIGINT UNSIGNED NOT NULL,
  action      VARCHAR(40)     NOT NULL,   -- LOCK_USER, UNLOCK_USER, CREATE_CATEGORY, UPDATE_CATEGORY,
                                          -- TOGGLE_CATEGORY, HIDE_PRODUCT, REMOVE_PRODUCT,
                                          -- HANDLE_REPORT, VIEW_ORDER
  target_type ENUM('USER','CATEGORY','PRODUCT','REPORT','ORDER') NOT NULL,
  target_id   BIGINT UNSIGNED NOT NULL,
  reason      VARCHAR(500)    NULL,
  created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_al_admin FOREIGN KEY (admin_id) REFERENCES users(id),
  INDEX idx_al_target (target_type, target_id),
  INDEX idx_al_admin  (admin_id, created_at)
) ENGINE=InnoDB;

-- =====================================================================
-- DỮ LIỆU MẪU TỐI THIỂU (tùy chọn)
-- =====================================================================
INSERT INTO categories (name, description) VALUES
  ('Sách - Giáo trình', 'Sách, giáo trình, tài liệu học tập'),
  ('Đồ điện tử', 'Laptop, điện thoại, phụ kiện'),
  ('Đồ gia dụng', 'Đồ dùng trong ký túc xá, phòng trọ'),
  ('Quần áo - Phụ kiện', 'Thời trang đã qua sử dụng'),
  ('Xe cộ', 'Xe đạp, xe máy, phụ kiện'),
  ('Khác', 'Các sản phẩm khác')
ON DUPLICATE KEY UPDATE description = VALUES(description), is_active = TRUE;

-- Tài khoản thử nghiệm. Tất cả tài khoản đều dùng mật khẩu 12345678.
-- Mật khẩu được lưu bằng BCrypt; không chèn mật khẩu thô vào cơ sở dữ liệu.
INSERT INTO users (
  id, full_name, email, phone, password_hash, role, status
) VALUES
  (1, 'Demo User', 'demo@marketplace.local', '0900000001',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   'USER', 'ACTIVE'),
  (1001, 'Nguyễn Minh Anh', 'user@cho2hand.local', '0900000002',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   'USER', 'ACTIVE'),
  (1002, 'Quản trị viên', 'admin@cho2hand.local', '0900000003',
   '$2a$10$24tbRqNDZ3RKpPqgj./J9OyrOfM5PyuTcZFEHgAZGfHqc3VT4528a',
   'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  full_name = VALUES(full_name),
  phone = VALUES(phone),
  password_hash = VALUES(password_hash),
  role = VALUES(role),
  status = 'ACTIVE',
  lock_reason = NULL,
  locked_until = NULL;
