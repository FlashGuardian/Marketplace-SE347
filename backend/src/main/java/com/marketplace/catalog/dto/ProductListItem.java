package com.marketplace.catalog.dto;

import java.time.LocalDateTime;

/** Một dòng trong danh sách sản phẩm (UC03.1). status là SELLING hoặc HELD; giao diện hiện nhãn "Đang giữ" cho HELD. */
public record ProductListItem(
        Long id,
        String name,
        long price,
        String conditionLevel,
        String status,
        String thumbnailUrl,
        LocalDateTime createdAt
) {
}
