package com.repository;

import com.exception.BankException;
import com.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction) throws BankException;
    List<Transaction> findByAccountNumber(String accountNumber);
    List<Transaction> findAll();
    int getNextId();
}
