package com.marketplace.auth.service;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException(String field) {
        super("An account with this " + field + " already exists");
    }
}
