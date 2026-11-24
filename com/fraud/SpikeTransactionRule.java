package com.fraud;

import com.model.Account;
import com.model.Transaction;

import java.util.List;

/**
 * Flags sudden spikes in transaction amounts compared to historic average.
 */
public class SpikeTransactionRule implements FraudRule {
    public static final double DEFAULT_MULTIPLIER = 5.0;
    public static final int MIN_REQUIRED_HISTORY = 3;

    private final double multiplier;

    public SpikeTransactionRule() {
        this(DEFAULT_MULTIPLIER);
    }

    public SpikeTransactionRule(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public String getRuleName() {
        return "Spike Transaction Rule";
    }

    @Override
    public FraudRuleResult evaluate(Transaction transaction, Account account, List<Transaction> history) {
        if (history != null && history.size() >= MIN_REQUIRED_HISTORY) {
            double sum = 0.0;
            for (Transaction t : history) {
                sum += t.getAmount();
            }
            double average = sum / history.size();
            if (transaction.getAmount() > average * multiplier) {
                return FraudRuleResult.flagged(RiskLevel.MEDIUM,
                        String.format("Amount ($%.2f) is over %.0fx the historical account average ($%.2f)",
                                transaction.getAmount(), multiplier, average));
            }
        }
        return FraudRuleResult.clean();
    }
}
