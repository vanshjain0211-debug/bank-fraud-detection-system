package com.repository;

import com.exception.BankException;
import com.model.Account;

import java.util.List;

public interface AccountRepository {
    void save(Account account) throws BankException;
    Account findByAccountNumber(String accountNumber);
    List<Account> findByOwner(String username);
    List<Account> findAll();
}
