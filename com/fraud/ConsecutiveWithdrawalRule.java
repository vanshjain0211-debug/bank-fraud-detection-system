package com.fraud;

import com.model.Account;
import com.model.Transaction;
import com.model.TransactionType;

import java.util.List;

/**
 * Detects multiple consecutive withdrawals draining the account rapidly.
 */
public class ConsecutiveWithdrawalRule implements FraudRule {
    public static final int CONSECUTIVE_LIMIT = 3;

    @Override
    public String getRuleName() {
        return "Consecutive Withdrawal Drain Rule";
    }

    @Override
    public FraudRuleResult evaluate(Transaction transaction, Account account, List<Transaction> history) {
        if (transaction.getType() != TransactionType.WITHDRAWAL) {
            return FraudRuleResult.clean();
        }

        if (history == null || history.size() < CONSECUTIVE_LIMIT - 1) {
            return FraudRuleResult.clean();
        }

        int count = 1;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).getType() == TransactionType.WITHDRAWAL) {
                count++;
            } else {
                break;
            }
        }

        if (count >= CONSECUTIVE_LIMIT && transaction.getAmount() > account.getBalance() * 0.5) {
            return FraudRuleResult.flagged(RiskLevel.HIGH,
                    String.format("Repeated heavy drain: %d consecutive withdrawals reducing balance", count));
        }

        return FraudRuleResult.clean();
    }
}
