package com.marketplace.auth.service;

public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
        super("This account has been locked");
    }
}
