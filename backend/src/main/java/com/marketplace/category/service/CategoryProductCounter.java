package com.marketplace.category.service;

/**
 * Đếm sản phẩm theo danh mục. Tách thành giao diện để UC19 không phụ thuộc entity Product
 * (UC06 làm song song); khi có Product entity thì thay phần cài đặt bằng repository của Product.
 */
public interface CategoryProductCounter {

    long countByCategoryId(Integer categoryId);
}
