package com.marketplace.catalog.repository;

import com.marketplace.catalog.dto.CategoryOption;
import com.marketplace.catalog.dto.ProductListItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Truy vấn đọc cho trang công khai. Dùng SQL trực tiếp trên bảng products, product_images, users,
 * categories để không phụ thuộc entity của UC06; mọi giá trị đều là tham số.
 */
@Repository
public class CatalogRepository {

    /** QĐ-05: danh sách hiện sản phẩm Đang bán và Đang giữ; Ẩn, Bị gỡ, Đã bán không hiện. */
    private static final String VISIBLE = """
            FROM products p
            JOIN users u ON u.id = p.seller_id
            WHERE p.status IN ('SELLING', 'HELD')
              AND u.status = 'ACTIVE'
            """;
    private static final String BY_CATEGORY = " AND p.category_id = :categoryId ";

    private final JdbcClient jdbc;

    public CatalogRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * QĐ-03: sản phẩm của tài khoản bị khóa không hiển thị công khai, nên lọc theo trạng thái chủ sản phẩm.
     * Mới nhất trước; ảnh đại diện là ảnh được đánh dấu thumbnail, nếu không có thì ảnh có thứ tự nhỏ nhất.
     */
    public List<ProductListItem> findVisible(Integer categoryId, int limit, int offset) {
        var statement = jdbc.sql("""
                        SELECT p.id, p.name, p.price, p.condition_level, p.status, p.created_at,
                               (SELECT pi.image_url FROM product_images pi
                                 WHERE pi.product_id = p.id
                                 ORDER BY pi.is_thumbnail DESC, pi.sort_order ASC
                                 LIMIT 1) AS thumbnail_url
                        """ + VISIBLE + (categoryId == null ? "" : BY_CATEGORY) + """
                        ORDER BY p.created_at DESC, p.id DESC
                        LIMIT :limit OFFSET :offset
                        """)
                .param("limit", limit)
                .param("offset", offset);
        if (categoryId != null) {
            statement = statement.param("categoryId", categoryId);
        }
        return statement.query((rs, row) -> new ProductListItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("price"),
                        rs.getString("condition_level"),
                        rs.getString("status"),
                        rs.getString("thumbnail_url"),
                        rs.getTimestamp("created_at").toLocalDateTime()))
                .list();
    }

    public long countVisible(Integer categoryId) {
        var statement = jdbc.sql("SELECT COUNT(*) " + VISIBLE + (categoryId == null ? "" : BY_CATEGORY));
        if (categoryId != null) {
            statement = statement.param("categoryId", categoryId);
        }
        return statement.query(Long.class).single();
    }

    public List<CategoryOption> findActiveCategories() {
        return jdbc.sql("SELECT id, name FROM categories WHERE is_active = TRUE ORDER BY id")
                .query((rs, row) -> new CategoryOption(rs.getInt("id"), rs.getString("name")))
                .list();
    }
}
