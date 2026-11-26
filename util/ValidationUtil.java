package com.util;

import com.exception.BankException;
import com.exception.InvalidTransactionException;

public class ValidationUtil {

    public static void validateCredentials(String username, String password) throws BankException {
        if (username == null || username.trim().isEmpty()) {
            throw new BankException("Username cannot be empty.");
        }
        if (password == null || password.length() < 4) {
            throw new BankException("Password must be at least 4 characters long.");
        }
    }

    public static void validateName(String name) throws BankException {
        if (name == null || name.trim().isEmpty()) {
            throw new BankException("Full name cannot be blank.");
        }
    }

    public static void validatePositiveAmount(double amount, String operation) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException(operation + " amount must be strictly greater than zero.");
        }
    }
}
