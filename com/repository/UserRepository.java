package com.repository;

import com.exception.BankException;
import com.model.User;

import java.util.List;

public interface UserRepository {
    void save(User user) throws BankException;
    User findByUsername(String username);
    List<User> findAll();
}
