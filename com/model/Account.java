package com.model;

import com.exception.AccountFrozenException;
import com.exception.BankException;
import com.exception.InsufficientFundsException;
import com.exception.InvalidTransactionException;

import java.time.LocalDateTime;

/**
 * Abstract base class for all bank accounts, demonstrating abstraction and encapsulation.
 */
public abstract class Account {
    private final String accountNumber;
    private final String ownerUsername;
    protected double balance;
    private AccountStatus status;
    private final LocalDateTime createdAt;

    public Account(String accountNumber, String ownerUsername, double initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerUsername = ownerUsername;
        this.balance = initialBalance;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public Account(String accountNumber, String ownerUsername, double balance, AccountStatus status, LocalDateTime createdAt) {
        this.accountNumber = accountNumber;
        this.ownerUsername = ownerUsername;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public double getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public void deposit(double amount) throws BankException {
        if (status == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Account " + accountNumber + " is FROZEN. Deposits are temporarily restricted.");
        }
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be strictly greater than 0.");
        }
        balance += amount;
    }

    /**
     * Polymorphic withdraw implementation defined by specific account types.
     */
    public abstract void withdraw(double amount) throws BankException;

    public abstract String getAccountType();

    @Override
    public String toString() {
        return String.format("[%s] Account: %s | Owner: %s | Balance: $%.2f | Status: %s",
                getAccountType(), accountNumber, ownerUsername, balance, status);
    }
}
