package com.service;

import com.fraud.FraudDetectionEngine;
import com.model.Account;
import com.model.FraudAlert;
import com.model.Transaction;

/**
 * Legacy adapter maintaining compatibility with earlier fraud checker design.
 */
public class FraudDetector implements FraudChecker {
    private final FraudDetectionEngine engine;

    public FraudDetector() {
        this.engine = new FraudDetectionEngine();
    }

    public FraudDetector(FraudDetectionEngine engine) {
        this.engine = engine;
    }

    @Override
    public FraudAlert check(Transaction transaction, Account account) {
        return engine.analyze(transaction, account, null);
    }
}
