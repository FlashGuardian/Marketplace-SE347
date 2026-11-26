package com.marketplace.catalog.service;

public class InvalidPriceRangeException extends RuntimeException {

    public InvalidPriceRangeException() {
        super("Khoảng giá không hợp lệ: giá không âm và giá thấp nhất không được lớn hơn giá cao nhất.");
    }
}
