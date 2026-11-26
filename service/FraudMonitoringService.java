package com.service;

import com.exception.BankException;
import com.model.FraudAlert;

import java.util.List;

public interface FraudMonitoringService {
    List<FraudAlert> getAllAlerts();
    List<FraudAlert> getPendingAlerts();
    void resolveAlert(int alertId) throws BankException;
    void dismissAlert(int alertId) throws BankException;
    void freezeAccount(String accountNumber, String reason) throws BankException;
    void unfreezeAccount(String accountNumber) throws BankException;
}
