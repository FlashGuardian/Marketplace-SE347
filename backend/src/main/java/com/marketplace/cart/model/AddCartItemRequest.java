package com.marketplace.cart.model;

import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(@NotNull Long productId) {
}
