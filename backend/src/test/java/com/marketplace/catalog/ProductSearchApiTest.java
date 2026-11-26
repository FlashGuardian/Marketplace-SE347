package com.marketplace.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** UC04.1 Tìm kiếm và lọc sản phẩm qua HTTP (công khai). Bảng được tạo tạm nếu chưa có entity, như CatalogApiTest. */
@SpringBootTest
@AutoConfigureMockMvc
class ProductSearchApiTest {

    static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 1, 8, 0);

    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    long seller;
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
        seller = user("uc04.active@example.com", "0933000001", "ACTIVE");
        lockedSeller = user("uc04.locked@example.com", "0933000002", "LOCKED");
        books = category("Sách - Giáo trình");
        tech = category("Đồ điện tử");
    }

    long user(String email, String phone, String status) {
        jdbc.update("DELETE FROM users WHERE email = ?", email);
        jdbc.update("INSERT INTO users (email, password_hash, full_name, phone, role, status) VALUES (?, 'x', 'Người bán thử', ?, 'USER', ?)",
                email, phone, status);
        return jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, email);
    }

    int category(String name) {
        jdbc.update("INSERT INTO categories (name, is_active, created_at, updated_at) VALUES (?, TRUE, ?, ?)",
                name, Timestamp.valueOf(BASE), Timestamp.valueOf(BASE));
        return jdbc.queryForObject("SELECT id FROM categories WHERE name = ?", Integer.class, name);
    }

    /** Sản phẩm với đủ các thuộc tính mà tìm kiếm quan tâm. */
    long product(long sellerId, int categoryId, String name, String description, long price, String condition,
                 String status, int minutesAfterBase) {
        Timestamp at = Timestamp.valueOf(BASE.plusMinutes(minutesAfterBase));
        jdbc.update("INSERT INTO products (seller_id, category_id, name, description, price, condition_level, status, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", sellerId, categoryId, name, description, price, condition, status, at, at);
        return jdbc.queryForObject("SELECT id FROM products WHERE name = ?", Long.class, name);
    }

    long simple(String name, long price, int minutes) {
        return product(seller, books, name, "mô tả chung", price, "GOOD", "SELLING", minutes);
    }

    MockHttpServletRequestBuilder search() {
        return get("/api/products/search");
    }

    // ===== Phạm vi hiển thị =====

    @Test
    void searchIsPublicAndOnlyReturnsSellingAndHeldFromActiveSellers() throws Exception {
        simple("Giáo trình A", 100000, 1);
        product(seller, books, "Giáo trình B", "m", 100000, "GOOD", "HELD", 2);
        product(seller, books, "Giáo trình C", "m", 100000, "GOOD", "HIDDEN", 3);
        product(seller, books, "Giáo trình D", "m", 100000, "GOOD", "REMOVED", 4);
        product(seller, books, "Giáo trình E", "m", 100000, "GOOD", "SOLD", 5);
        product(lockedSeller, books, "Giáo trình F", "m", 100000, "GOOD", "SELLING", 6);

        mvc.perform(search().param("q", "giáo trình"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.items[*].name", contains("Giáo trình B", "Giáo trình A")))
                .andExpect(jsonPath("$.items[0].status").value("HELD"))
                .andExpect(jsonPath("$.items[1].status").value("SELLING"));
    }

    @Test
    void withoutAnyParameterSearchListsEverythingNewestFirst() throws Exception {
        simple("Cũ", 1, 1);
        simple("Mới", 1, 9);

        mvc.perform(search())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*].name", contains("Mới", "Cũ")))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(20));
    }

    // ===== Từ khóa =====

    @Test
    void keywordIsCaseInsensitiveAndMatchesNameOrDescription() throws Exception {
        simple("Giáo trình Giải tích 1", 45000, 1);
        product(seller, tech, "Máy tính Casio", "Dùng cho môn Giải Tích rất tốt", 150000, "GOOD", "SELLING", 2);
        simple("Áo khoác gió", 220000, 3);

        mvc.perform(search().param("q", "GIẢI TÍCH"))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.items[*].name", contains("Máy tính Casio", "Giáo trình Giải tích 1")));
        mvc.perform(search().param("q", "  áo khoác  "))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Áo khoác gió"));
    }

    @Test
    void noMatchGivesAnEmptyListNotAnError() throws Exception {
        simple("Giáo trình A", 1, 1);

        mvc.perform(search().param("q", "không có sản phẩm này"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void aBlankKeywordIsIgnored() throws Exception {
        simple("Một", 1, 1);
        simple("Hai", 1, 2);

        mvc.perform(search().param("q", "   "))
                .andExpect(jsonPath("$.totalItems").value(2));
    }

    @Test
    void likeWildcardsInTheKeywordAreTypedCharactersNotWildcards() throws Exception {
        simple("Giảm 100% hôm nay", 1, 1);
        simple("Hàng bình thường", 1, 2);
        simple("Mã_số_A", 1, 3);

        mvc.perform(search().param("q", "%"))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Giảm 100% hôm nay"));
        mvc.perform(search().param("q", "_"))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Mã_số_A"));
        mvc.perform(search().param("q", "!"))
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    void aKeywordThatIsTooLongIsRejected() throws Exception {
        mvc.perform(search().param("q", "a".repeat(101)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        mvc.perform(search().param("q", "a".repeat(100))).andExpect(status().isOk());
    }

    // ===== Bộ lọc =====

    @Test
    void categoryAndKeywordFiltersAreCombined() throws Exception {
        product(seller, books, "Sách Java", "m", 90000, "GOOD", "SELLING", 1);
        product(seller, tech, "Sách điện tử Java", "m", 90000, "GOOD", "SELLING", 2);
        product(seller, books, "Sách Python", "m", 90000, "GOOD", "SELLING", 3);

        mvc.perform(search().param("q", "java").param("categoryId", String.valueOf(books)))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Sách Java"));
    }

    @Test
    void priceRangeIsInclusiveAndEitherEndMayBeOmitted() throws Exception {
        simple("Rẻ", 50000, 1);
        simple("Vừa", 100000, 2);
        simple("Đắt", 200000, 3);

        mvc.perform(search().param("minPrice", "50000").param("maxPrice", "100000").param("sort", "priceAsc"))
                .andExpect(jsonPath("$.items[*].name", contains("Rẻ", "Vừa")));
        mvc.perform(search().param("minPrice", "100001"))
                .andExpect(jsonPath("$.items[*].name", contains("Đắt")));
        mvc.perform(search().param("maxPrice", "49999"))
                .andExpect(jsonPath("$.totalItems").value(0));
    }

    @Test
    void anInvertedOrNegativePriceRangeIsRejected() throws Exception {
        mvc.perform(search().param("minPrice", "200000").param("maxPrice", "100000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PRICE_RANGE"));
        mvc.perform(search().param("minPrice", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PRICE_RANGE"));
        mvc.perform(search().param("minPrice", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        mvc.perform(search().param("minPrice", "100000").param("maxPrice", "100000")).andExpect(status().isOk());
    }

    @Test
    void conditionLevelFilterKeepsOnlyThatCondition() throws Exception {
        product(seller, books, "Như mới", "m", 1, "LIKE_NEW", "SELLING", 1);
        product(seller, books, "Tốt", "m", 1, "GOOD", "SELLING", 2);
        product(seller, books, "Cũ", "m", 1, "POOR", "SELLING", 3);

        mvc.perform(search().param("conditionLevel", "LIKE_NEW"))
                .andExpect(jsonPath("$.items[*].name", contains("Như mới")));
        mvc.perform(search().param("conditionLevel", "WRONG"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    // ===== Sắp xếp =====

    @Test
    void sortByPriceOrNewestWithNewestAsTheTieBreak() throws Exception {
        simple("Giá 100 cũ", 100000, 1);
        simple("Giá 100 mới", 100000, 5);
        simple("Giá 50", 50000, 3);
        simple("Giá 200", 200000, 4);

        mvc.perform(search().param("sort", "priceAsc"))
                .andExpect(jsonPath("$.items[*].name", contains("Giá 50", "Giá 100 mới", "Giá 100 cũ", "Giá 200")));
        mvc.perform(search().param("sort", "priceDesc"))
                .andExpect(jsonPath("$.items[*].name", contains("Giá 200", "Giá 100 mới", "Giá 100 cũ", "Giá 50")));
        mvc.perform(search().param("sort", "newest"))
                .andExpect(jsonPath("$.items[*].name", contains("Giá 100 mới", "Giá 200", "Giá 50", "Giá 100 cũ")));
        mvc.perform(search().param("sort", "cheapest"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    // ===== Phân trang =====

    @Test
    void paginationCountsOnlyTheFilteredResults() throws Exception {
        for (int i = 1; i <= 23; i++) {
            simple("Laptop " + String.format("%02d", i), 1000L * i, i);
        }
        simple("Không liên quan", 1, 40);

        mvc.perform(search().param("q", "laptop").param("size", "10").param("page", "3").param("sort", "priceAsc"))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name").value("Laptop 21"))
                .andExpect(jsonPath("$.totalItems").value(23))
                .andExpect(jsonPath("$.totalPages").value(3));
        mvc.perform(search().param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PAGINATION"));
    }

    @Test
    void anItemHasTheSameFieldsAsTheListEndpoint() throws Exception {
        long id = simple("Tai nghe", 450000, 1);
        jdbc.update("INSERT INTO product_images (product_id, image_url, sort_order, is_thumbnail, created_at) VALUES (?, '/a.jpg', 1, TRUE, ?)",
                id, Timestamp.valueOf(BASE));

        mvc.perform(search().param("q", "tai nghe"))
                .andExpect(jsonPath("$.items[0].id").value(id))
                .andExpect(jsonPath("$.items[0].price").value(450000))
                .andExpect(jsonPath("$.items[0].conditionLevel").value("GOOD"))
                .andExpect(jsonPath("$.items[0].thumbnailUrl").value("/a.jpg"))
                .andExpect(jsonPath("$.items[0].createdAt").exists());
    }
}
