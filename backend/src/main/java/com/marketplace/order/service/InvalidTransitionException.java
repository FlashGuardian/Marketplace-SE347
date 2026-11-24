package com.marketplace.order.service;

public class InvalidTransitionException extends RuntimeException {

    public InvalidTransitionException() {
        super("Chuyển trạng thái đơn hàng không hợp lệ");
    }
}
