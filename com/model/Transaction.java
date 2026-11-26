package com.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int id;
    private final String accountNumber;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final String relatedAccount;
    private final LocalDateTime timestamp;

    public Transaction(int id, String accountNumber, TransactionType type, double amount,
                       double balanceAfter, String relatedAccount) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedAccount = relatedAccount;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(int id, String accountNumber, TransactionType type, double amount,
                       double balanceAfter, String relatedAccount, LocalDateTime timestamp) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedAccount = relatedAccount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getRelatedAccount() {
        return relatedAccount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("#%04d | %s | %-12s | Amount: $%9.2f | Balance: $%9.2f | Related: %s",
                id, timestamp.format(FORMATTER), type, amount, balanceAfter, relatedAccount);
    }
}
