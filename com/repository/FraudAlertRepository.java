package com.repository;

import com.exception.BankException;
import com.model.FraudAlert;

import java.util.List;

public interface FraudAlertRepository {
    void save(FraudAlert alert) throws BankException;
    FraudAlert findById(int alertId);
    List<FraudAlert> findByAccountNumber(String accountNumber);
    List<FraudAlert> findAll();
    int getNextId();
}
