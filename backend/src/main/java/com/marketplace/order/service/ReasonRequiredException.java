package com.marketplace.order.service;

public class ReasonRequiredException extends RuntimeException {

    public ReasonRequiredException() {
        super("Cần nhập lý do cho thao tác này");
    }
}
