package com.marketplace.order.service;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Không tìm thấy đơn hàng: " + orderId);
    }
}
