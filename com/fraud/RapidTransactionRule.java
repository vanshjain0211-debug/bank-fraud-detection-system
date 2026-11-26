package com.fraud;

import com.model.Account;
import com.model.Transaction;

import java.time.Duration;
import java.util.List;

/**
 * Velocity check: Flags rapid burst of transactions occurring within a very short timeframe.
 */
public class RapidTransactionRule implements FraudRule {
    public static final int TIME_WINDOW_SECONDS = 60;
    public static final int MAX_TRANSACTIONS_IN_WINDOW = 3;

    @Override
    public String getRuleName() {
        return "Rapid Transaction Velocity Rule";
    }

    @Override
    public FraudRuleResult evaluate(Transaction transaction, Account account, List<Transaction> history) {
        if (history == null || history.isEmpty()) {
            return FraudRuleResult.clean();
        }

        int recentCount = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            Transaction past = history.get(i);
            long secondsBetween = Math.abs(Duration.between(past.getTimestamp(), transaction.getTimestamp()).getSeconds());
            if (secondsBetween <= TIME_WINDOW_SECONDS) {
                recentCount++;
            } else {
                break;
            }
        }

        if (recentCount >= MAX_TRANSACTIONS_IN_WINDOW) {
            return FraudRuleResult.flagged(RiskLevel.CRITICAL,
                    String.format("Velocity violation: %d transactions executed within %d seconds",
                            recentCount + 1, TIME_WINDOW_SECONDS));
        }

        return FraudRuleResult.clean();
    }
}
