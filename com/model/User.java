package com.model;

/** Abstract base class for everyone who can log in. */
public abstract class User {
    private final String username;
    private final String password;
    private final String name;

    protected User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    /** Each subclass describes its own role (polymorphism). */
    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + ": " + name + " (" + username + ")";
    }
}