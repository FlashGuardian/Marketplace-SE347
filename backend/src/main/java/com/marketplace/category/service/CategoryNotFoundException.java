package com.marketplace.category.service;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException() {
        super("Không tìm thấy danh mục.");
    }
}
