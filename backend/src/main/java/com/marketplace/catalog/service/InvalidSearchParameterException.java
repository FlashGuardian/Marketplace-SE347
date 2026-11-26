package com.marketplace.catalog.service;

public class InvalidSearchParameterException extends RuntimeException {

    public InvalidSearchParameterException(String message) {
        super(message);
    }
}
