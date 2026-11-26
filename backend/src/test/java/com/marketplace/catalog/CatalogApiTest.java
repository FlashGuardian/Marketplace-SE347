package com.marketplace.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC03 qua HTTP: danh sách sản phẩm công khai (QĐ-05) và danh mục đang hoạt động. Các bảng products,
 * product_images, categories chưa có entity ở thời điểm viết, nên test tự tạo bảng (chỉ các cột bắt buộc)
 * nếu chưa có; khi entity có mặt thì dùng đúng bảng của entity.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CatalogApiTest {

    static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 1, 8, 0);

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    long activeSeller;
    long lockedSeller;
    int books;
    int tech;

    @BeforeEach
    void setUp() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS categories (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, "
                + "description VARCHAR(500), is_active BOOLEAN NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS products (id BIGINT AUTO_INCREMENT PRIMARY KEY, seller_id BIGINT NOT NULL, "
                + "category_id INT NOT NULL, name VARCHAR(200) NOT NULL, description VARCHAR(2000) NOT NULL, price DECIMAL(12,0) NOT NULL, "
                + "condition_level VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL)");
        jdbc.execute("CREATE TABLE IF NOT EXISTS product_images (id BIGINT AUTO_INCREMENT PRIMARY KEY, product_id BIGINT NOT NULL, "
                + "image_url VARCHAR(500) NOT NULL, sort_order TINYINT NOT NULL, is_thumbnail BOOLEAN NOT NULL, created_at TIMESTAMP NOT NULL)");
        jdbc.update("DELETE FROM product_images");
        jdbc.update("DELETE FROM products");
        jdbc.update("DELETE FROM categories");

        activeSeller = user("uc03.active@example.com", "0922000001", "ACTIVE");
        lockedSeller = user("uc03.locked@example.com", "0922000002", "LOCKED");
        books = category("Sách - Giáo trình", true);
        tech = category("Đồ điện tử", true);
    }

    long user(String email, String phone, String status) {
        jdbc.update("DELETE FROM users WHERE email = ?", email);
        jdbc.update("INSERT INTO users (email, password_hash, full_name, phone, role, status) VALUES (?, 'x', 'Người bán thử', ?, 'USER', ?)",
                email, phone, status);
        return jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, email);
    }

    int category(String name, boolean active) {
        jdbc.update("INSERT INTO categories (name, is_active, created_at, updated_at) VALUES (?, ?, ?, ?)",
                name, active, Timestamp.valueOf(BASE), Timestamp.valueOf(BASE));
        return jdbc.queryForObject("SELECT id FROM categories WHERE name = ?", Integer.class, name);
    }

    long product(long sellerId, int categoryId, String name, String status, int minutesAfterBase) {
        Timestamp at = Timestamp.valueOf(BASE.plusMinutes(minutesAfterBase));
        jdbc.update("INSERT INTO products (seller_id, category_id, name, description, price, condition_level, status, created_at, updated_at) "
                        + "VALUES (?, ?, ?, 'mô tả', 150000, 'GOOD', ?, ?, ?)",
                sellerId, categoryId, name, status, at, at);
        return jdbc.queryForObject("SELECT id FROM products WHERE name = ?", Long.class, name);
    }

    void image(long productId, String url, int sortOrder, boolean thumbnail) {
        jdbc.update("INSERT INTO product_images (product_id, image_url, sort_order, is_thumbnail, created_at) VALUES (?, ?, ?, ?, ?)",
                productId, url, sortOrder, thumbnail, Timestamp.valueOf(BASE));
    }

    // ===== UC03.1 Danh sách sản phẩm =====

    @Test
    void theListIsPublicAndNeedsNoToken() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void onlySellingAndHeldProductsAreListedHiddenRemovedAndSoldAreNot() throws Exception {
        product(activeSeller, books, "Đang bán", "SELLING", 1);
        product(activeSeller, books, "Đang giữ", "HELD", 2);
        product(activeSeller, books, "Đang ẩn", "HIDDEN", 3);
        product(activeSeller, books, "Bị gỡ", "REMOVED", 4);
        product(activeSeller, books, "Đã bán", "SOLD", 5);

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.items[*].name", contains("Đang giữ", "Đang bán")))
                .andExpect(jsonPath("$.items[0].status").value("HELD"))
                .andExpect(jsonPath("$.items[1].status").value("SELLING"));
    }

    @Test
    void anItemHasTheDocumentedFields() throws Exception {
        long id = product(activeSeller, tech, "Tai nghe Sony", "SELLING", 1);
        image(id, "/uploads/tai-nghe.jpg", 1, true);

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(id))
                .andExpect(jsonPath("$.items[0].name").value("Tai nghe Sony"))
                .andExpect(jsonPath("$.items[0].price").value(150000))
                .andExpect(jsonPath("$.items[0].conditionLevel").value("GOOD"))
                .andExpect(jsonPath("$.items[0].status").value("SELLING"))
                .andExpect(jsonPath("$.items[0].thumbnailUrl").value("/uploads/tai-nghe.jpg"))
                .andExpect(jsonPath("$.items[0].createdAt").exists());
    }

    @Test
    void newestProductsComeFirst() throws Exception {
        product(activeSeller, books, "Cũ nhất", "SELLING", 1);
        product(activeSeller, books, "Mới nhất", "SELLING", 30);
        product(activeSeller, books, "Ở giữa", "SELLING", 10);

        mvc.perform(get("/api/products"))
                .andExpect(jsonPath("$.items[*].name", contains("Mới nhất", "Ở giữa", "Cũ nhất")));
    }

    @Test
    void theThumbnailIsTheMarkedImageElseTheFirstOneElseNull() throws Exception {
        long marked = product(activeSeller, books, "Có thumbnail", "SELLING", 3);
        image(marked, "/a-2.jpg", 2, false);
        image(marked, "/a-3.jpg", 3, true);
        long unmarked = product(activeSeller, books, "Không đánh dấu", "SELLING", 2);
        image(unmarked, "/b-2.jpg", 2, false);
        image(unmarked, "/b-1.jpg", 1, false);
        product(activeSeller, books, "Không ảnh", "SELLING", 1);

        mvc.perform(get("/api/products"))
                .andExpect(jsonPath("$.items[0].thumbnailUrl").value("/a-3.jpg"))
                .andExpect(jsonPath("$.items[1].thumbnailUrl").value("/b-1.jpg"))
                .andExpect(jsonPath("$.items[2].thumbnailUrl").doesNotExist());
    }

    @Test
    void productsOfALockedSellerAreNotShownPublicly() throws Exception {
        product(activeSeller, books, "Của người bán thường", "SELLING", 1);
        product(lockedSeller, books, "Của người bị khóa", "SELLING", 2);

        mvc.perform(get("/api/products"))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Của người bán thường"));
    }

    @Test
    void theCategoryFilterKeepsOnlyThatCategory() throws Exception {
        product(activeSeller, books, "Sách A", "SELLING", 1);
        product(activeSeller, tech, "Máy B", "SELLING", 2);
        product(activeSeller, tech, "Máy C", "HELD", 3);

        mvc.perform(get("/api/products").param("categoryId", String.valueOf(tech)))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.items[*].name", contains("Máy C", "Máy B")));
        mvc.perform(get("/api/products").param("categoryId", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void paginationUsesPagesFromOneWithTotals() throws Exception {
        for (int i = 1; i <= 25; i++) {
            product(activeSeller, books, "Sản phẩm " + String.format("%02d", i), "SELLING", i);
        }

        mvc.perform(get("/api/products").param("page", "1").param("size", "10"))
                .andExpect(jsonPath("$.items", hasSize(10)))
                .andExpect(jsonPath("$.items[0].name").value("Sản phẩm 25"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalItems").value(25))
                .andExpect(jsonPath("$.totalPages").value(3));
        mvc.perform(get("/api/products").param("page", "3").param("size", "10"))
                .andExpect(jsonPath("$.items", hasSize(5)))
                .andExpect(jsonPath("$.items[4].name").value("Sản phẩm 01"));
        mvc.perform(get("/api/products").param("page", "4").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalItems").value(25));
        mvc.perform(get("/api/products"))
                .andExpect(jsonPath("$.items", hasSize(20)))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void invalidPaginationOrCategoryIdIsRejected() throws Exception {
        for (String[] bad : new String[][]{{"page", "0"}, {"page", "-1"}, {"size", "0"}, {"size", "51"}}) {
            mvc.perform(get("/api/products").param(bad[0], bad[1]))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_PAGINATION"));
        }
        mvc.perform(get("/api/products").param("size", "50")).andExpect(status().isOk());
        mvc.perform(get("/api/products").param("categoryId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        mvc.perform(get("/api/products").param("page", "x"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    // ===== UC03.2 Danh mục đang hoạt động =====

    @Test
    void categoriesListsOnlyActiveOnesWithIdAndName() throws Exception {
        category("Đã vô hiệu hóa", false);

        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(books))
                .andExpect(jsonPath("$[0].name").value("Sách - Giáo trình"))
                .andExpect(jsonPath("$[1].id").value(tech));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM categories", Integer.class)).isEqualTo(3);
    }
}
