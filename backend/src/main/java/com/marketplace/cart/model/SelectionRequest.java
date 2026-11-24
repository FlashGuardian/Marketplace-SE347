package com.marketplace.cart.model;

import jakarta.validation.constraints.NotNull;

public record SelectionRequest(@NotNull Boolean selected) {
}
