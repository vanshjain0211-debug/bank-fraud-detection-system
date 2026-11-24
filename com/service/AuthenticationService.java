package com.service;

import com.exception.AuthenticationException;
import com.exception.BankException;
import com.model.Customer;
import com.model.User;
import com.repository.UserRepository;
import com.util.IdGenerator;
import com.util.ValidationUtil;

public class AuthenticationService {
    private final UserRepository userRepository;
    private int nextCustomerId = 1;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        initSequence();
    }

    private void initSequence() {
        for (User u : userRepository.findAll()) {
            if (u instanceof Customer c) {
                try {
                    String idStr = c.getCustomerId().replace("CUS", "");
                    int id = Integer.parseInt(idStr);
                    if (id >= nextCustomerId) nextCustomerId = id + 1;
                } catch (Exception ignored) {}
            }
        }
    }

    public User authenticate(String username, String password) throws BankException {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.checkPassword(password)) {
            throw new AuthenticationException("Invalid username or password.");
        }
        return user;
    }

    public Customer registerCustomer(String name, String username, String password) throws BankException {
        ValidationUtil.validateName(name);
        ValidationUtil.validateCredentials(username, password);

        if (userRepository.findByUsername(username) != null) {
            throw new BankException("Username '" + username + "' is already registered.");
        }

        Customer customer = new Customer(IdGenerator.formatCustomerId(nextCustomerId++), name.trim(), username.trim(), password);
        userRepository.save(customer);
        return customer;
    }
}
