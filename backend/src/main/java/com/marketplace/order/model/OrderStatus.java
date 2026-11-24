package com.marketplace.order.model;

import java.util.EnumSet;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    SHIPPING("Đang giao nhận"),
    COMPLETED("Hoàn thành"),
    REJECTED("Đã từ chối"),
    CANCELLED("Đã hủy");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, REJECTED, CANCELLED).contains(next);
            case CONFIRMED -> EnumSet.of(SHIPPING, CANCELLED).contains(next);
            case SHIPPING -> next == COMPLETED;
            case COMPLETED, REJECTED, CANCELLED -> false;
        };
    }
}
