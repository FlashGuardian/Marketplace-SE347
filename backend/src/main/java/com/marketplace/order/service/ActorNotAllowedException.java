package com.marketplace.order.service;

public class ActorNotAllowedException extends RuntimeException {

    public ActorNotAllowedException() {
        super("Người thực hiện không có quyền chuyển trạng thái đơn hàng này");
    }
}
