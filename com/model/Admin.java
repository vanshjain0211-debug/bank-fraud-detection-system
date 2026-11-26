package com.model;

public class Admin extends User {

    public Admin(String name, String username, String password) {
        super(name, username, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}