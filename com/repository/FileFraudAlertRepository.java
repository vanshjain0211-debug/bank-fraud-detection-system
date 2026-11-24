package com.repository;

import com.exception.BankException;
import com.fraud.RiskLevel;
import com.model.AlertStatus;
import com.model.FraudAlert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileFraudAlertRepository implements FraudAlertRepository {
    private final String filePath;
    private final List<FraudAlert> cache = new ArrayList<>();
    private int nextId = 1;

    public FileFraudAlertRepository(String filePath) {
        this.filePath = filePath;
        load();
    }

    private synchronized void load() {
        cache.clear();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            try {
                if (path.getParent() != null) Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    int id = Integer.parseInt(parts[0]);
                    String accNo = parts[1];
                    int txnId = Integer.parseInt(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    String reason = parts[4].replace(";", ",");
                    RiskLevel level = RiskLevel.valueOf(parts[5]);
                    AlertStatus status = AlertStatus.valueOf(parts[6]);
                    LocalDateTime time = LocalDateTime.parse(parts[7]);

                    cache.add(new FraudAlert(id, accNo, txnId, amount, reason, level, status, time));
                    if (id >= nextId) nextId = id + 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: could not read fraud alerts file: " + e.getMessage());
        }
    }

    private synchronized void persist() throws BankException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (FraudAlert fa : cache) {
                writer.println(String.format("%d,%s,%d,%.2f,%s,%s,%s,%s",
                        fa.getAlertId(), fa.getAccountNumber(), fa.getTransactionId(), fa.getAmount(),
                        fa.getReason().replace(",", ";"), fa.getRiskLevel(), fa.getStatus(), fa.getTimestamp()));
            }
        } catch (IOException e) {
            throw new BankException("Failed to persist alerts to file: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void save(FraudAlert alert) throws BankException {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getAlertId() == alert.getAlertId()) {
                cache.set(i, alert);
                persist();
                return;
            }
        }
        cache.add(alert);
        if (alert.getAlertId() >= nextId) nextId = alert.getAlertId() + 1;
        persist();
    }

    @Override
    public synchronized FraudAlert findById(int alertId) {
        for (FraudAlert fa : cache) {
            if (fa.getAlertId() == alertId) return fa;
        }
        return null;
    }

    @Override
    public synchronized List<FraudAlert> findByAccountNumber(String accountNumber) {
        List<FraudAlert> res = new ArrayList<>();
        if (accountNumber == null) return res;
        for (FraudAlert fa : cache) {
            if (fa.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                res.add(fa);
            }
        }
        return res;
    }

    @Override
    public synchronized List<FraudAlert> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized int getNextId() {
        return nextId++;
    }
}
