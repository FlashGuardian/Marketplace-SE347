package com.marketplace.catalog.dto;

import java.util.List;

/** Khuôn phân trang chung của tài liệu API: {items, page, size, totalItems, totalPages}. page bắt đầu từ 1. */
public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {

    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalItems) {
        int totalPages = (int) ((totalItems + size - 1) / size);
        return new PageResponse<>(items, page, size, totalItems, totalPages);
    }
}
