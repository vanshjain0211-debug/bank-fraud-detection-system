package com.repository;

import com.exception.BankException;
import com.model.Admin;
import com.model.Customer;
import com.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {
    private final String filePath;
    private final List<User> cache = new ArrayList<>();

    public FileUserRepository(String filePath) {
        this.filePath = filePath;
        load();
        ensureDefaultAdmin();
    }

    private void ensureDefaultAdmin() {
        if (findByUsername("admin") == null) {
            Admin admin = new Admin("System Admin", "admin", "admin123");
            try {
                save(admin);
            } catch (BankException ignored) {}
        }
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
                if (parts.length >= 4) {
                    String role = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String name = parts[3];
                    if ("Admin".equalsIgnoreCase(role)) {
                        cache.add(new Admin(name, username, password));
                    } else if ("Customer".equalsIgnoreCase(role)) {
                        String custId = parts.length > 4 ? parts[4] : "CUS1";
                        cache.add(new Customer(custId, name, username, password));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read users file: " + e.getMessage());
        }
    }

    private synchronized void persist() throws BankException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (User u : cache) {
                if (u instanceof Admin admin) {
                    writer.println(String.format("Admin,%s,%s,%s,",
                            escape(admin.getUsername()), escape(admin.checkPassword("") ? "" : adminPassword(admin)), escape(admin.getName())));
                } else if (u instanceof Customer cust) {
                    writer.println(String.format("Customer,%s,%s,%s,%s",
                            escape(cust.getUsername()), escape(customerPassword(cust)), escape(cust.getName()), escape(cust.getCustomerId())));
                }
            }
        } catch (IOException e) {
            throw new BankException("Failed to persist users to file: " + e.getMessage(), e);
        }
    }

    // Reflective or field access helper to serialize stored password cleanly
    private String adminPassword(Admin admin) {
        try {
            var field = User.class.getDeclaredField("password");
            field.setAccessible(true);
            return (String) field.get(admin);
        } catch (Exception e) {
            return "admin123";
        }
    }

    private String customerPassword(Customer customer) {
        try {
            var field = User.class.getDeclaredField("password");
            field.setAccessible(true);
            return (String) field.get(customer);
        } catch (Exception e) {
            return "password";
        }
    }

    private String escape(String val) {
        return val == null ? "" : val.replace(",", ";");
    }

    @Override
    public synchronized void save(User user) throws BankException {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getUsername().equalsIgnoreCase(user.getUsername())) {
                cache.set(i, user);
                persist();
                return;
            }
        }
        cache.add(user);
        persist();
    }

    @Override
    public synchronized User findByUsername(String username) {
        if (username == null) return null;
        for (User u : cache) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public synchronized List<User> findAll() {
        return new ArrayList<>(cache);
    }
}
