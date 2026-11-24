package com.model;

import java.util.ArrayList;

public class Customer extends User {
    private final String customerId;
    private final ArrayList<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String name, String username, String password) {
        super(name, username, password);
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public ArrayList<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    @Override
    public String getRole() {
        return "Customer";
    }
}