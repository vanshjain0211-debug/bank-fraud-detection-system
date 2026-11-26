package com.repository;

import com.exception.BankException;
import com.model.Transaction;
import com.model.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// JDBC implementation of transaction repository
public class JdbcTransactionRepository implements TransactionRepository {

    private Transaction mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String accNo = rs.getString("account_number");
        TransactionType type = TransactionType.valueOf(rs.getString("type"));
        double amount = rs.getDouble("amount");
        double balanceAfter = rs.getDouble("balance_after");
        String related = rs.getString("related_account");
        LocalDateTime time = LocalDateTime.parse(rs.getString("timestamp"));
        return new Transaction(id, accNo, type, amount, balanceAfter, related, time);
    }

    @Override
    public void save(Transaction transaction) throws BankException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions (id, account_number, type, amount, balance_after, related_account, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, transaction.getId());
            ps.setString(2, transaction.getAccountNumber());
            ps.setString(3, transaction.getType().name());
            ps.setDouble(4, transaction.getAmount());
            ps.setDouble(5, transaction.getBalanceAfter());
            ps.setString(6, transaction.getRelatedAccount());
            ps.setString(7, transaction.getTimestamp().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BankException(e.getMessage());
        }
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        List<Transaction> list = new ArrayList<Transaction>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions WHERE account_number = ?")) {
            ps.setString(1, accountNumber);
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
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<Transaction>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    @Override
    public int getNextId() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT MAX(id) FROM transactions");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
        }
        return 1;
    }
}
