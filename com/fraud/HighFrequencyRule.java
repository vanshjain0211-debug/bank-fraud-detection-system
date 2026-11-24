package com.fraud;

import com.model.Account;
import com.model.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Checks if the number of daily transactions on an account exceeds a safe threshold.
 */
public class HighFrequencyRule implements FraudRule {
    public static final int DAILY_LIMIT = 10;

    @Override
    public String getRuleName() {
        return "High Daily Frequency Rule";
    }

    @Override
    public FraudRuleResult evaluate(Transaction transaction, Account account, List<Transaction> history) {
        if (history == null || history.isEmpty()) {
            return FraudRuleResult.clean();
        }

        LocalDate today = transaction.getTimestamp().toLocalDate();
        int todayCount = 0;
        for (Transaction past : history) {
            if (past.getTimestamp().toLocalDate().isEqual(today)) {
                todayCount++;
            }
        }

        if (todayCount >= DAILY_LIMIT) {
            return FraudRuleResult.flagged(RiskLevel.MEDIUM,
                    String.format("High daily frequency: %d transactions already performed today (limit: %d)",
                            todayCount + 1, DAILY_LIMIT));
        }

        return FraudRuleResult.clean();
    }
}
