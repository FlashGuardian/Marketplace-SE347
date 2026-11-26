package com.marketplace.catalog.repository;

import com.marketplace.catalog.dto.CategoryOption;
import com.marketplace.catalog.dto.ProductListItem;
import com.marketplace.catalog.dto.ProductFilter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Truy vấn đọc cho trang công khai (UC03, UC04). Dùng SQL trực tiếp trên bảng products, product_images, users,
 * categories để không phụ thuộc entity của UC06; mọi giá trị đều là tham số.
 */
@Repository
public class CatalogRepository {

    /** QĐ-05: danh sách và tìm kiếm đều hiện sản phẩm Đang bán và Đang giữ; Ẩn, Bị gỡ, Đã bán không hiện. */
    private static final String VISIBLE = """
            FROM products p
            JOIN users u ON u.id = p.seller_id
            WHERE p.status IN ('SELLING', 'HELD')
              AND u.status = 'ACTIVE'
            """;

    private final JdbcClient jdbc;

    public CatalogRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * QĐ-03: sản phẩm của tài khoản bị khóa không hiển thị công khai, nên lọc theo trạng thái chủ sản phẩm.
     * Ảnh đại diện là ảnh được đánh dấu thumbnail, nếu không có thì ảnh có thứ tự nhỏ nhất.
     */
    public List<ProductListItem> findVisible(ProductFilter filter, int limit, int offset) {
        Map<String, Object> params = new LinkedHashMap<>();
        String where = conditions(filter, params);
        params.put("limit", limit);
        params.put("offset", offset);
        return jdbc.sql("""
                        SELECT p.id, p.name, p.price, p.condition_level, p.status, p.created_at,
                               (SELECT pi.image_url FROM product_images pi
                                 WHERE pi.product_id = p.id
                                 ORDER BY pi.is_thumbnail DESC, pi.sort_order ASC
                                 LIMIT 1) AS thumbnail_url
                        """ + VISIBLE + where
                        + " ORDER BY " + filter.sort().orderBy()
                        + " LIMIT :limit OFFSET :offset")
                .params(params)
                .query((rs, row) -> new ProductListItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("price"),
                        rs.getString("condition_level"),
                        rs.getString("status"),
                        rs.getString("thumbnail_url"),
                        rs.getTimestamp("created_at").toLocalDateTime()))
                .list();
    }

    public long countVisible(ProductFilter filter) {
        Map<String, Object> params = new LinkedHashMap<>();
        String where = conditions(filter, params);
        return jdbc.sql("SELECT COUNT(*) " + VISIBLE + where)
                .params(params)
                .query(Long.class)
                .single();
    }

    /**
     * Ghép các điều kiện lọc có mặt. Chỉ ghép những đoạn SQL cố định; mọi giá trị người dùng nhập
     * đều đi qua tham số. Từ khóa tìm theo tên và mô tả, không phân biệt hoa thường (QĐ-06); việc
     * không phân biệt dấu do collation utf8mb4_unicode_ci của cơ sở dữ liệu đảm nhiệm.
     */
    private static String conditions(ProductFilter filter, Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        if (filter.categoryId() != null) {
            sql.append(" AND p.category_id = :categoryId");
            params.put("categoryId", filter.categoryId());
        }
        if (filter.keyword() != null) {
            sql.append(" AND (LOWER(p.name) LIKE :keyword ESCAPE '!' OR LOWER(p.description) LIKE :keyword ESCAPE '!')");
            params.put("keyword", "%" + filter.keyword().toLowerCase(Locale.ROOT) + "%");
        }
        if (filter.minPrice() != null) {
            sql.append(" AND p.price >= :minPrice");
            params.put("minPrice", filter.minPrice());
        }
        if (filter.maxPrice() != null) {
            sql.append(" AND p.price <= :maxPrice");
            params.put("maxPrice", filter.maxPrice());
        }
        if (filter.conditionLevel() != null) {
            sql.append(" AND p.condition_level = :conditionLevel");
            params.put("conditionLevel", filter.conditionLevel());
        }
        return sql.append(' ').toString();
    }

    public List<CategoryOption> findActiveCategories() {
        return jdbc.sql("SELECT id, name FROM categories WHERE is_active = TRUE ORDER BY id")
                .query((rs, row) -> new CategoryOption(rs.getInt("id"), rs.getString("name")))
                .list();
    }
}
