package com.marketplace.category.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Đếm trực tiếp trên bảng products, tính cả sản phẩm Ẩn và Bị gỡ vì chúng vẫn giữ tham chiếu danh mục. */
@Component
public class JdbcCategoryProductCounter implements CategoryProductCounter {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryProductCounter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long countByCategoryId(Integer categoryId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE category_id = ?", Long.class, categoryId);
        return count == null ? 0 : count;
    }
}
