package com.service;

import com.model.Account;
import com.model.FraudAlert;
import com.model.Transaction;

/** Contract for any fraud-detection strategy. */
public interface FraudChecker {

    /**
     * Checks a transaction that has not yet been added to the account history.
     *
     * @return a FraudAlert if the transaction looks suspicious, otherwise null
     */
    FraudAlert check(Transaction transaction, Account account);
}