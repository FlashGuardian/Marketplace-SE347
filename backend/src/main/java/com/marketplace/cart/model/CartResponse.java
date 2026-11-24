package com.marketplace.cart.model;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(List<CartSellerGroup> sellers, BigDecimal selectedTotal, boolean canCheckout) {
}
