package com.marketplace.cart.model;

import java.util.List;

public record CartSellerGroup(Long sellerId, String sellerName, List<CartItemResponse> items) {
}
