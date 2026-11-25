package com.marketplace.category.service;

public class CategoryHasProductsException extends RuntimeException {

    public CategoryHasProductsException() {
        super("Danh mục đang có sản phẩm nên không thể xóa, chỉ có thể vô hiệu hóa.");
    }
}
