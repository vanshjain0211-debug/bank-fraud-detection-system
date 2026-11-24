package com.service;

import com.exception.*;
import com.fraud.FraudDetectionEngine;
import com.fraud.RiskLevel;
import com.model.*;
import com.repository.AccountRepository;
import com.repository.FraudAlertRepository;
import com.repository.TransactionRepository;
import com.util.IdGenerator;
import com.util.ValidationUtil;

import java.util.List;

public class BankingServiceImpl implements BankingService {
    private final AccountRepository accountRepo;
    private final TransactionRepository txnRepo;
    private final FraudAlertRepository alertRepo;
    private final FraudDetectionEngine fraudEngine;
    private int nextAccNumber = 1001;

    public BankingServiceImpl(AccountRepository accountRepo, TransactionRepository txnRepo,
                              FraudAlertRepository alertRepo, FraudDetectionEngine fraudEngine) {
        this.accountRepo = accountRepo;
        this.txnRepo = txnRepo;
        this.alertRepo = alertRepo;
        this.fraudEngine = fraudEngine;
        initSequence();
    }

    private void initSequence() {
        for (Account a : accountRepo.findAll()) {
            try {
                String numStr = a.getAccountNumber().replace("ACC", "");
                int num = Integer.parseInt(numStr);
                if (num >= nextAccNumber) nextAccNumber = num + 1;
            } catch (Exception ignored) {}
        }
    }

    @Override
    public Account openSavingsAccount(Customer customer, double initialDeposit) throws BankException {
        if (initialDeposit < 0) {
            throw new InvalidTransactionException("Initial deposit cannot be negative.");
        }
        String accNo = IdGenerator.formatAccountNumber(nextAccNumber++);
        SavingsAccount account = new SavingsAccount(accNo, customer.getUsername(), 0.0);
        accountRepo.save(account);
        customer.addAccount(account);

        if (initialDeposit > 0) {
            account.deposit(initialDeposit);
            accountRepo.save(account);
            recordTransaction(account, TransactionType.DEPOSIT, initialDeposit, "-", true);
        }
        return account;
    }

    @Override
    public Account openCheckingAccount(Customer customer, double initialDeposit) throws BankException {
        if (initialDeposit < 0) {
            throw new InvalidTransactionException("Initial deposit cannot be negative.");
        }
        String accNo = IdGenerator.formatAccountNumber(nextAccNumber++);
        CheckingAccount account = new CheckingAccount(accNo, customer.getUsername(), 0.0);
        accountRepo.save(account);
        customer.addAccount(account);

        if (initialDeposit > 0) {
            account.deposit(initialDeposit);
            accountRepo.save(account);
            recordTransaction(account, TransactionType.DEPOSIT, initialDeposit, "-", true);
        }
        return account;
    }

    @Override
    public void deposit(Customer customer, String accountNumber, double amount) throws BankException {
        ValidationUtil.validatePositiveAmount(amount, "Deposit");
        Account account = getOwnedAccount(customer, accountNumber);
        account.deposit(amount);
        accountRepo.save(account);
        recordTransaction(account, TransactionType.DEPOSIT, amount, "-", true);
    }

    @Override
    public void withdraw(Customer customer, String accountNumber, double amount) throws BankException {
        ValidationUtil.validatePositiveAmount(amount, "Withdrawal");
        Account account = getOwnedAccount(customer, accountNumber);
        account.withdraw(amount);
        accountRepo.save(account);
        recordTransaction(account, TransactionType.WITHDRAWAL, amount, "-", true);
    }

    @Override
    public void transfer(Customer customer, String fromAccount, String toAccount, double amount) throws BankException {
        ValidationUtil.validatePositiveAmount(amount, "Transfer");
        if (fromAccount.equalsIgnoreCase(toAccount)) {
            throw new InvalidTransactionException("Cannot transfer funds to the same account.");
        }

        Account from = getOwnedAccount(customer, fromAccount);
        Account to = accountRepo.findByAccountNumber(toAccount);
        if (to == null) {
            throw new ResourceNotFoundException("Destination account '" + toAccount + "' not found.");
        }
        if (to.getStatus() == AccountStatus.FROZEN) {
            throw new AccountFrozenException("Destination account is FROZEN and cannot receive transfers.");
        }

        from.withdraw(amount);
        to.deposit(amount);

        accountRepo.save(from);
        accountRepo.save(to);

        recordTransaction(from, TransactionType.TRANSFER_OUT, amount, to.getAccountNumber(), true);
        recordTransaction(to, TransactionType.TRANSFER_IN, amount, from.getAccountNumber(), false);
    }

    @Override
    public List<Account> getCustomerAccounts(Customer customer) {
        return accountRepo.findByOwner(customer.getUsername());
    }

    @Override
    public Account getOwnedAccount(Customer customer, String accountNumber) throws BankException {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null || !account.getOwnerUsername().equalsIgnoreCase(customer.getUsername())) {
            throw new BankException("You do not own an account with number " + accountNumber + ".");
        }
        return account;
    }

    @Override
    public List<Transaction> getAccountHistory(Customer customer, String accountNumber) throws BankException {
        getOwnedAccount(customer, accountNumber); // verifies ownership
        return txnRepo.findByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    private void recordTransaction(Account account, TransactionType type, double amount, String related, boolean analyzeFraud) throws BankException {
        int txnId = txnRepo.getNextId();
        Transaction t = new Transaction(txnId, account.getAccountNumber(), type, amount, account.getBalance(), related);
        txnRepo.save(t);

        if (analyzeFraud) {
            List<Transaction> history = txnRepo.findByAccountNumber(account.getAccountNumber());
            FraudAlert alert = fraudEngine.analyze(t, account, history);
            if (alert != null) {
                alertRepo.save(alert);
                // Critical risk automatically freezes the account to protect customer funds!
                if (alert.getRiskLevel() == RiskLevel.CRITICAL) {
                    account.setStatus(AccountStatus.FROZEN);
                    accountRepo.save(account);
                    System.out.println("🚨 CRITICAL SECURITY ALERT: Account " + account.getAccountNumber() + " auto-frozen due to velocity violation.");
                }
            }
        }
    }
}
