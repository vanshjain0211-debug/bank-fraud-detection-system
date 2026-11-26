package com.fraud;

import com.model.Account;
import com.model.FraudAlert;
import com.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite Fraud Detection Engine running all configured rules.
 */
public class FraudDetectionEngine {
    private final List<FraudRule> rules = new ArrayList<>();
    private int nextAlertId = 1;

    public FraudDetectionEngine() {
        registerDefaultRules();
    }

    public void registerDefaultRules() {
        rules.add(new LargeTransactionRule());
        rules.add(new SpikeTransactionRule());
        rules.add(new RapidTransactionRule());
        rules.add(new HighFrequencyRule());
        rules.add(new ConsecutiveWithdrawalRule());
    }

    public void addRule(FraudRule rule) {
        rules.add(rule);
    }

    public List<FraudRule> getRules() {
        return new ArrayList<>(rules);
    }

    public int getNextAlertId() {
        return nextAlertId;
    }

    public void setNextAlertId(int id) {
        this.nextAlertId = id;
    }

    public FraudAlert analyze(Transaction transaction, Account account, List<Transaction> history) {
        FraudRuleResult worstResult = null;

        for (FraudRule rule : rules) {
            FraudRuleResult result = rule.evaluate(transaction, account, history);
            if (result.isSuspicious()) {
                if (worstResult == null || result.getRiskLevel().ordinal() > worstResult.getRiskLevel().ordinal()) {
                    worstResult = result;
                }
            }
        }

        if (worstResult != null) {
            return new FraudAlert(nextAlertId++, transaction.getAccountNumber(), transaction.getId(),
                    transaction.getAmount(), worstResult.getReason(), worstResult.getRiskLevel());
        }

        return null;
    }
}
