package com.service;

import com.exception.BankException;
import com.model.Account;
import com.model.Customer;
import com.model.Transaction;

import java.util.List;

public interface BankingService {
    Account openSavingsAccount(Customer customer, double initialDeposit) throws BankException;
    Account openCheckingAccount(Customer customer, double initialDeposit) throws BankException;
    void deposit(Customer customer, String accountNumber, double amount) throws BankException;
    void withdraw(Customer customer, String accountNumber, double amount) throws BankException;
    void transfer(Customer customer, String fromAccount, String toAccount, double amount) throws BankException;
    List<Account> getCustomerAccounts(Customer customer);
    Account getOwnedAccount(Customer customer, String accountNumber) throws BankException;
    List<Transaction> getAccountHistory(Customer customer, String accountNumber) throws BankException;
    List<Account> getAllAccounts();
}
