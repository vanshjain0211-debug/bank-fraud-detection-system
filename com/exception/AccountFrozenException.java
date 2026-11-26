package com.exception;

public class AccountFrozenException extends BankException {
    private static final long serialVersionUID = 1L;

    public AccountFrozenException(String message) {
        super(message);
    }
}
