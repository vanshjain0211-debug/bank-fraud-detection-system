package com.model;

import com.exception.AccountFrozenException;
import com.exception.BankException;
import com.exception.InsufficientFundsException;
import com.exception.InvalidTransactionException;

import java.time.LocalDateTime;

/**
 * Checking Account offering overdraft protection up to a specified limit.
 */
public class CheckingAccount extends Account {
    public static final double DEFAULT_OVERDRAFT_LIMIT = 1000.00;

    private final double overdraftLimit;

    public CheckingAccount(String accountNumber, String ownerUsername, double initialBalance) {
        super(accountNumber, ownerUsername, initialBalance);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    public CheckingAccount(String accountNumber, String ownerUsername, double balance,
                           AccountStatus status, LocalDateTime createdAt, double overdraftLimit) {
        super(accountNumber, ownerUsername, balance, status, createdAt);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public void withdraw(double amount) throws BankException {
        if (getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Account " + getAccountNumber() + " is FROZEN. Withdrawals are blocked.");
        }
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be greater than 0.");
        }
        if (balance - amount < -overdraftLimit) {
            throw new InsufficientFundsException(String.format(
                    "Withdrawal denied. Exceeds overdraft limit ($%.2f). Current balance: $%.2f",
                    overdraftLimit, balance));
        }
        balance -= amount;
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }
}
