package com.fraud;

public class FraudRuleResult {
    private final boolean suspicious;
    private final RiskLevel riskLevel;
    private final String reason;

    public FraudRuleResult(boolean suspicious, RiskLevel riskLevel, String reason) {
        this.suspicious = suspicious;
        this.riskLevel = riskLevel;
        this.reason = reason;
    }

    public static FraudRuleResult clean() {
        return new FraudRuleResult(false, RiskLevel.LOW, "Clean");
    }

    public static FraudRuleResult flagged(RiskLevel level, String reason) {
        return new FraudRuleResult(true, level, reason);
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getReason() {
        return reason;
    }
}
