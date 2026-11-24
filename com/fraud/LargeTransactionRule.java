package com.fraud;

import com.model.Account;
import com.model.Transaction;

import java.util.List;

/**
 * Flags any single transaction whose amount meets or exceeds the threshold.
 */
public class LargeTransactionRule implements FraudRule {
    public static final double DEFAULT_LIMIT = 50000.00;
    private final double limit;

    public LargeTransactionRule() {
        this(DEFAULT_LIMIT);
    }

    public LargeTransactionRule(double limit) {
        this.limit = limit;
    }

    @Override
    public String getRuleName() {
        return "Large Transaction Rule";
    }

    @Override
    public FraudRuleResult evaluate(Transaction transaction, Account account, List<Transaction> history) {
        if (transaction.getAmount() >= limit) {
            return FraudRuleResult.flagged(RiskLevel.HIGH,
                    String.format("Large transaction amount ($%.2f) exceeds limit ($%.2f)",
                            transaction.getAmount(), limit));
        }
        return FraudRuleResult.clean();
    }
}
