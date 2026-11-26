package com.repository;

import com.exception.BankException;
import com.model.Admin;
import com.model.Customer;
import com.model.User;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// JDBC implementation of user repository
public class JdbcUserRepository implements UserRepository {

    public JdbcUserRepository() {
        try {
            if (findByUsername("admin") == null) {
                save(new Admin("System Admin", "admin", "admin123"));
            }
        } catch (Exception ignored) {
        }
    }

    private String getPassword(User u) {
        try {
            Field f = User.class.getDeclaredField("password");
            f.setAccessible(true);
            return (String) f.get(u);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void save(User user) throws BankException {
        User existing = findByUsername(user.getUsername());
        if (existing == null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, name, role, customer_id) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, user.getUsername());
                ps.setString(2, getPassword(user));
                ps.setString(3, user.getName());
                ps.setString(4, user.getRole());
                if (user instanceof Customer) {
                    ps.setString(5, ((Customer) user).getCustomerId());
                } else {
                    ps.setString(5, "");
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new BankException(e.getMessage());
            }
        } else {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ?, name = ?, role = ?, customer_id = ? WHERE username = ?")) {
                ps.setString(1, getPassword(user));
                ps.setString(2, user.getName());
                ps.setString(3, user.getRole());
                if (user instanceof Customer) {
                    ps.setString(4, ((Customer) user).getCustomerId());
                } else {
                    ps.setString(4, "");
                }
                ps.setString(5, user.getUsername());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new BankException(e.getMessage());
            }
        }
    }

    @Override
    public User findByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String uname = rs.getString("username");
                    String pass = rs.getString("password");
                    String name = rs.getString("name");
                    if ("Admin".equalsIgnoreCase(role)) {
                        return new Admin(name, uname, pass);
                    } else {
                        String custId = rs.getString("customer_id");
                        return new Customer(custId, name, uname, pass);
                    }
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<User>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String role = rs.getString("role");
                String uname = rs.getString("username");
                String pass = rs.getString("password");
                String name = rs.getString("name");
                if ("Admin".equalsIgnoreCase(role)) {
                    list.add(new Admin(name, uname, pass));
                } else {
                    String custId = rs.getString("customer_id");
                    list.add(new Customer(custId, name, uname, pass));
                }
            }
        } catch (SQLException e) {
        }
        return list;
    }
}
