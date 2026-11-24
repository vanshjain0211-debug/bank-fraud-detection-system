package com.repository;

import com.exception.BankException;
import com.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileAccountRepository implements AccountRepository {
    private final String filePath;
    private final List<Account> cache = new ArrayList<>();

    public FileAccountRepository(String filePath) {
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
                if (parts.length >= 6) {
                    String type = parts[0];
                    String accNo = parts[1];
                    String owner = parts[2];
                    double balance = Double.parseDouble(parts[3]);
                    AccountStatus status = AccountStatus.valueOf(parts[4]);
                    LocalDateTime createdAt = LocalDateTime.parse(parts[5]);

                    if ("SAVINGS".equalsIgnoreCase(type)) {
                        double minBal = parts.length > 6 ? Double.parseDouble(parts[6]) : SavingsAccount.DEFAULT_MIN_BALANCE;
                        double rate = parts.length > 7 ? Double.parseDouble(parts[7]) : SavingsAccount.DEFAULT_INTEREST_RATE;
                        cache.add(new SavingsAccount(accNo, owner, balance, status, createdAt, minBal, rate));
                    } else if ("CHECKING".equalsIgnoreCase(type)) {
                        double overdraft = parts.length > 6 ? Double.parseDouble(parts[6]) : CheckingAccount.DEFAULT_OVERDRAFT_LIMIT;
                        cache.add(new CheckingAccount(accNo, owner, balance, status, createdAt, overdraft));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: could not read accounts file: " + e.getMessage());
        }
    }

    private synchronized void persist() throws BankException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (Account a : cache) {
                if (a instanceof SavingsAccount sa) {
                    writer.println(String.format("SAVINGS,%s,%s,%.2f,%s,%s,%.2f,%.4f",
                            sa.getAccountNumber(), sa.getOwnerUsername(), sa.getBalance(),
                            sa.getStatus(), sa.getCreatedAt(), sa.getMinimumBalance(), sa.getInterestRate()));
                } else if (a instanceof CheckingAccount ca) {
                    writer.println(String.format("CHECKING,%s,%s,%.2f,%s,%s,%.2f",
                            ca.getAccountNumber(), ca.getOwnerUsername(), ca.getBalance(),
                            ca.getStatus(), ca.getCreatedAt(), ca.getOverdraftLimit()));
                }
            }
        } catch (IOException e) {
            throw new BankException("Failed to persist accounts to file: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void save(Account account) throws BankException {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getAccountNumber().equalsIgnoreCase(account.getAccountNumber())) {
                cache.set(i, account);
                persist();
                return;
            }
        }
        cache.add(account);
        persist();
    }

    @Override
    public synchronized Account findByAccountNumber(String accountNumber) {
        if (accountNumber == null) return null;
        for (Account a : cache) {
            if (a.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return a;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Account> findByOwner(String username) {
        List<Account> result = new ArrayList<>();
        if (username == null) return result;
        for (Account a : cache) {
            if (a.getOwnerUsername().equalsIgnoreCase(username)) {
                result.add(a);
            }
        }
        return result;
    }

    @Override
    public synchronized List<Account> findAll() {
        return new ArrayList<>(cache);
    }
}
