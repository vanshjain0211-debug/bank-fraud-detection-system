package com.service;

import com.exception.BankException;
import com.exception.ResourceNotFoundException;
import com.model.Account;
import com.model.AccountStatus;
import com.model.AlertStatus;
import com.model.FraudAlert;
import com.repository.AccountRepository;
import com.repository.FraudAlertRepository;

import java.util.ArrayList;
import java.util.List;

public class FraudMonitoringServiceImpl implements FraudMonitoringService {
    private final FraudAlertRepository alertRepo;
    private final AccountRepository accountRepo;

    public FraudMonitoringServiceImpl(FraudAlertRepository alertRepo, AccountRepository accountRepo) {
        this.alertRepo = alertRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public List<FraudAlert> getAllAlerts() {
        return alertRepo.findAll();
    }

    @Override
    public List<FraudAlert> getPendingAlerts() {
        List<FraudAlert> res = new ArrayList<>();
        for (FraudAlert a : alertRepo.findAll()) {
            if (a.getStatus() == AlertStatus.PENDING) {
                res.add(a);
            }
        }
        return res;
    }

    @Override
    public void resolveAlert(int alertId) throws BankException {
        FraudAlert alert = alertRepo.findById(alertId);
        if (alert == null) {
            throw new ResourceNotFoundException("Fraud Alert #" + alertId + " not found.");
        }
        alert.setStatus(AlertStatus.RESOLVED);
        alertRepo.save(alert);
    }

    @Override
    public void dismissAlert(int alertId) throws BankException {
        FraudAlert alert = alertRepo.findById(alertId);
        if (alert == null) {
            throw new ResourceNotFoundException("Fraud Alert #" + alertId + " not found.");
        }
        alert.setStatus(AlertStatus.DISMISSED);
        alertRepo.save(alert);
    }

    @Override
    public void freezeAccount(String accountNumber, String reason) throws BankException {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account " + accountNumber + " not found.");
        }
        account.setStatus(AccountStatus.FROZEN);
        accountRepo.save(account);
    }

    @Override
    public void unfreezeAccount(String accountNumber) throws BankException {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account " + accountNumber + " not found.");
        }
        account.setStatus(AccountStatus.ACTIVE);
        accountRepo.save(account);
    }
}
