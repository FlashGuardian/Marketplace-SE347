package com.marketplace.cart.model;

import java.math.BigDecimal;
import java.util.List;

public record CartPreviewResponse(List<Item> items) {

    public record Item(Long productId, String name, BigDecimal price) {
    }
}
