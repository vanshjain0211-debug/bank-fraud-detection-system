package com.fraud;

import com.model.Account;
import com.model.Transaction;

import java.util.List;

/**
 * Strategy interface for fraud detection rules.
 */
public interface FraudRule {
    String getRuleName();
    FraudRuleResult evaluate(Transaction transaction, Account account, List<Transaction> history);
}
