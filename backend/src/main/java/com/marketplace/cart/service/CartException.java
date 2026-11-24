package com.marketplace.cart.service;

import org.springframework.http.HttpStatus;

public class CartException extends RuntimeException {

    private final Code code;

    public CartException(Code code) {
        super(code.message);
        this.code = code;
    }

    public String getCode() {
        return code.name();
    }

    public HttpStatus getStatus() {
        return code.status;
    }

    public enum Code {
        OWN_PRODUCT(HttpStatus.FORBIDDEN, "Không thể thêm sản phẩm của chính bạn"),
        PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"),
        CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Sản phẩm không có trong giỏ hàng"),
        ALREADY_IN_CART(HttpStatus.CONFLICT, "Sản phẩm đã có trong giỏ hàng"),
        PRODUCT_NOT_AVAILABLE(HttpStatus.CONFLICT, "Sản phẩm hiện không khả dụng");

        private final HttpStatus status;
        private final String message;

        Code(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
