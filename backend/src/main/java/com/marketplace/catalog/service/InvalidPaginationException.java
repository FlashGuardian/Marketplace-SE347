package com.marketplace.catalog.service;

public class InvalidPaginationException extends RuntimeException {

    public InvalidPaginationException() {
        super("Tham số phân trang không hợp lệ: page từ 1 trở lên, size từ 1 đến 50.");
    }
}
