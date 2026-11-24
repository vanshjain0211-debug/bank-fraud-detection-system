package com.model;

import com.fraud.RiskLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FraudAlert {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int alertId;
    private final String accountNumber;
    private final int transactionId;
    private final double amount;
    private final String reason;
    private final RiskLevel riskLevel;
    private AlertStatus status;
    private final LocalDateTime timestamp;

    public FraudAlert(int alertId, String accountNumber, int transactionId, double amount,
                      String reason, RiskLevel riskLevel) {
        this.alertId = alertId;
        this.accountNumber = accountNumber;
        this.transactionId = transactionId;
        this.amount = amount;
        this.reason = reason;
        this.riskLevel = riskLevel;
        this.status = AlertStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    public FraudAlert(int alertId, String accountNumber, int transactionId, double amount,
                      String reason, RiskLevel riskLevel, AlertStatus status, LocalDateTime timestamp) {
        this.alertId = alertId;
        this.accountNumber = accountNumber;
        this.transactionId = transactionId;
        this.amount = amount;
        this.reason = reason;
        this.riskLevel = riskLevel;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getAlertId() {
        return alertId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("ALERT-%03d [%s] | %s | Acc: %s | Txn: #%d | Amount: $%.2f | Status: %s | Reason: %s",
                alertId, riskLevel, timestamp.format(FORMATTER), accountNumber, transactionId, amount, status, reason);
    }
}
