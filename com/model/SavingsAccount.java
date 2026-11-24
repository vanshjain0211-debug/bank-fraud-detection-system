package com.model;

import com.exception.AccountFrozenException;
import com.exception.BankException;
import com.exception.InsufficientFundsException;
import com.exception.InvalidTransactionException;

import java.time.LocalDateTime;

/**
 * Savings Account enforcing a minimum balance requirement and earning interest.
 */
public class SavingsAccount extends Account {
    public static final double DEFAULT_MIN_BALANCE = 500.00;
    public static final double DEFAULT_INTEREST_RATE = 0.035; // 3.5%

    private final double minimumBalance;
    private final double interestRate;

    public SavingsAccount(String accountNumber, String ownerUsername, double initialBalance) {
        super(accountNumber, ownerUsername, initialBalance);
        this.minimumBalance = DEFAULT_MIN_BALANCE;
        this.interestRate = DEFAULT_INTEREST_RATE;
    }

    public SavingsAccount(String accountNumber, String ownerUsername, double balance,
                          AccountStatus status, LocalDateTime createdAt, double minimumBalance, double interestRate) {
        super(accountNumber, ownerUsername, balance, status, createdAt);
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public void withdraw(double amount) throws BankException {
        if (getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Account " + getAccountNumber() + " is FROZEN. Withdrawals are blocked.");
        }
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be greater than 0.");
        }
        if (balance - amount < minimumBalance) {
            throw new InsufficientFundsException(String.format(
                    "Withdrawal denied. Savings account requires a minimum balance of $%.2f. Current balance: $%.2f",
                    minimumBalance, balance));
        }
        balance -= amount;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}
