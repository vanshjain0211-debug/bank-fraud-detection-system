package com.repository;

import com.exception.BankException;
import com.model.Transaction;
import com.model.TransactionType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileTransactionRepository implements TransactionRepository {
    private final String filePath;
    private final List<Transaction> cache = new ArrayList<>();
    private int nextId = 1;

    public FileTransactionRepository(String filePath) {
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
                if (parts.length >= 7) {
                    int id = Integer.parseInt(parts[0]);
                    String accNo = parts[1];
                    TransactionType type = TransactionType.valueOf(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    double balAfter = Double.parseDouble(parts[4]);
                    String related = parts[5];
                    LocalDateTime time = LocalDateTime.parse(parts[6]);

                    cache.add(new Transaction(id, accNo, type, amount, balAfter, related, time));
                    if (id >= nextId) nextId = id + 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: could not read transactions file: " + e.getMessage());
        }
    }

    private synchronized void persist() throws BankException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (Transaction t : cache) {
                writer.println(String.format(java.util.Locale.US, "%d,%s,%s,%.2f,%.2f,%s,%s",
                        t.getId(), t.getAccountNumber(), t.getType(), t.getAmount(),
                        t.getBalanceAfter(), t.getRelatedAccount(), t.getTimestamp()));
            }
        } catch (IOException e) {
            throw new BankException("Failed to persist transactions to file: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void save(Transaction transaction) throws BankException {
        cache.add(transaction);
        if (transaction.getId() >= nextId) nextId = transaction.getId() + 1;
        persist();
    }

    @Override
    public synchronized List<Transaction> findByAccountNumber(String accountNumber) {
        List<Transaction> result = new ArrayList<>();
        if (accountNumber == null) return result;
        for (Transaction t : cache) {
            if (t.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public synchronized List<Transaction> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized int getNextId() {
        return nextId++;
    }
}
