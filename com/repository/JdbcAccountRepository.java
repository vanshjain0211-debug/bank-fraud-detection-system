package com.repository;

import com.exception.BankException;
import com.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// JDBC implementation of account repository
public class JdbcAccountRepository implements AccountRepository {

    private Account mapRow(ResultSet rs) throws SQLException {
        String accNo = rs.getString("account_number");
        String owner = rs.getString("owner_username");
        double balance = rs.getDouble("balance");
        AccountStatus status = AccountStatus.valueOf(rs.getString("status"));
        LocalDateTime createdAt = LocalDateTime.parse(rs.getString("created_at"));
        String type = rs.getString("account_type");
        if ("SAVINGS".equalsIgnoreCase(type)) {
            double minBal = rs.getDouble("minimum_balance");
            double rate = rs.getDouble("interest_rate");
            return new SavingsAccount(accNo, owner, balance, status, createdAt, minBal, rate);
        } else {
            double overdraft = rs.getDouble("overdraft_limit");
            return new CheckingAccount(accNo, owner, balance, status, createdAt, overdraft);
        }
    }

    @Override
    public void save(Account account) throws BankException {
        Account existing = findByAccountNumber(account.getAccountNumber());
        if (existing == null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO accounts (account_number, owner_username, balance, status, created_at, account_type, minimum_balance, interest_rate, overdraft_limit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, account.getAccountNumber());
                ps.setString(2, account.getOwnerUsername());
                ps.setDouble(3, account.getBalance());
                ps.setString(4, account.getStatus().name());
                ps.setString(5, account.getCreatedAt().toString());
                ps.setString(6, account.getAccountType());
                if (account instanceof SavingsAccount) {
                    SavingsAccount sa = (SavingsAccount) account;
                    ps.setDouble(7, sa.getMinimumBalance());
                    ps.setDouble(8, sa.getInterestRate());
                    ps.setDouble(9, 0.0);
                } else {
                    CheckingAccount ca = (CheckingAccount) account;
                    ps.setDouble(7, 0.0);
                    ps.setDouble(8, 0.0);
                    ps.setDouble(9, ca.getOverdraftLimit());
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new BankException(e.getMessage());
            }
        } else {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET owner_username = ?, balance = ?, status = ?, created_at = ?, account_type = ?, minimum_balance = ?, interest_rate = ?, overdraft_limit = ? WHERE account_number = ?")) {
                ps.setString(1, account.getOwnerUsername());
                ps.setDouble(2, account.getBalance());
                ps.setString(3, account.getStatus().name());
                ps.setString(4, account.getCreatedAt().toString());
                ps.setString(5, account.getAccountType());
                if (account instanceof SavingsAccount) {
                    SavingsAccount sa = (SavingsAccount) account;
                    ps.setDouble(6, sa.getMinimumBalance());
                    ps.setDouble(7, sa.getInterestRate());
                    ps.setDouble(8, 0.0);
                } else {
                    CheckingAccount ca = (CheckingAccount) account;
                    ps.setDouble(6, 0.0);
                    ps.setDouble(7, 0.0);
                    ps.setDouble(8, ca.getOverdraftLimit());
                }
                ps.setString(9, account.getAccountNumber());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new BankException(e.getMessage());
            }
        }
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE account_number = ?")) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public List<Account> findByOwner(String username) {
        List<Account> list = new ArrayList<Account>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE owner_username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
        }
        return list;
    }

    @Override
    public List<Account> findAll() {
        List<Account> list = new ArrayList<Account>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
        }
        return list;
    }
}
