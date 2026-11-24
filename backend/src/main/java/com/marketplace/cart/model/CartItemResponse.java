package com.marketplace.cart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
        Long productId,
        String name,
        BigDecimal price,
        boolean selected,
        boolean available,
        LocalDateTime addedAt
) {
}
