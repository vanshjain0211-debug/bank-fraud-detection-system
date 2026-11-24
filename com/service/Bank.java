package com.service;

import com.exception.BankException;
import com.fraud.FraudDetectionEngine;
import com.model.Account;
import com.model.Customer;
import com.model.FraudAlert;
import com.model.Transaction;
import com.model.User;
import com.repository.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade class unifying authentication, banking operations, and fraud monitoring.
 */
public class Bank {
    private final AuthenticationService authService;
    private final BankingService bankingService;
    private final FraudMonitoringService fraudService;

    public Bank() {
        this("data");
    }

    public Bank(String dataDir) {
        UserRepository userRepo = new FileUserRepository(dataDir + "/users.csv");
        AccountRepository accountRepo = new FileAccountRepository(dataDir + "/accounts.csv");
        TransactionRepository txnRepo = new FileTransactionRepository(dataDir + "/transactions.csv");
        FraudAlertRepository alertRepo = new FileFraudAlertRepository(dataDir + "/alerts.csv");
        FraudDetectionEngine fraudEngine = new FraudDetectionEngine();

        this.authService = new AuthenticationService(userRepo);
        this.bankingService = new BankingServiceImpl(accountRepo, txnRepo, alertRepo, fraudEngine);
        this.fraudService = new FraudMonitoringServiceImpl(alertRepo, accountRepo);
    }

    // ---------- Auth ----------
    public User login(String username, String password) throws BankException {
        return authService.authenticate(username, password);
    }

    public Customer registerCustomer(String name, String username, String password) throws BankException {
        return authService.registerCustomer(name, username, password);
    }

    // ---------- Banking Operations ----------
    public Account createAccount(Customer customer, double initialDeposit) throws BankException {
        return bankingService.openSavingsAccount(customer, initialDeposit);
    }

    public Account openSavingsAccount(Customer customer, double initialDeposit) throws BankException {
        return bankingService.openSavingsAccount(customer, initialDeposit);
    }

    public Account openCheckingAccount(Customer customer, double initialDeposit) throws BankException {
        return bankingService.openCheckingAccount(customer, initialDeposit);
    }

    public void deposit(Customer customer, String accountNumber, double amount) throws BankException {
        bankingService.deposit(customer, accountNumber, amount);
    }

    public void withdraw(Customer customer, String accountNumber, double amount) throws BankException {
        bankingService.withdraw(customer, accountNumber, amount);
    }

    public void transfer(Customer customer, String fromNumber, String toNumber, double amount) throws BankException {
        bankingService.transfer(customer, fromNumber, toNumber, amount);
    }

    public ArrayList<Account> getCustomerAccounts(Customer customer) {
        return new ArrayList<>(bankingService.getCustomerAccounts(customer));
    }

    public Account getOwnedAccount(Customer customer, String accountNumber) throws BankException {
        return bankingService.getOwnedAccount(customer, accountNumber);
    }

    public ArrayList<Transaction> getHistory(Customer customer, String accountNumber) throws BankException {
        return new ArrayList<>(bankingService.getAccountHistory(customer, accountNumber));
    }

    public ArrayList<Account> getAllAccounts() {
        return new ArrayList<>(bankingService.getAllAccounts());
    }

    // ---------- Fraud & Admin Operations ----------
    public ArrayList<FraudAlert> getAlerts() {
        return new ArrayList<>(fraudService.getAllAlerts());
    }

    public ArrayList<FraudAlert> getPendingAlerts() {
        return new ArrayList<>(fraudService.getPendingAlerts());
    }

    public void resolveAlert(int alertId) throws BankException {
        fraudService.resolveAlert(alertId);
    }

    public void dismissAlert(int alertId) throws BankException {
        fraudService.dismissAlert(alertId);
    }

    public void freezeAccount(String accountNumber, String reason) throws BankException {
        fraudService.freezeAccount(accountNumber, reason);
    }

    public void unfreezeAccount(String accountNumber) throws BankException {
        fraudService.unfreezeAccount(accountNumber);
    }
}
