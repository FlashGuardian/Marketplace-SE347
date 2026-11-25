package com.marketplace.category.service;

public class CategoryNameExistsException extends RuntimeException {

    public CategoryNameExistsException() {
        super("Tên danh mục đã tồn tại.");
    }
}
