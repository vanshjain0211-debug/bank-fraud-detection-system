package com.repository;

import com.exception.BankException;
import com.fraud.RiskLevel;
import com.model.AlertStatus;
import com.model.FraudAlert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// JDBC implementation of fraud alert repository
public class JdbcFraudAlertRepository implements FraudAlertRepository {

    private FraudAlert mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("alert_id");
        String accNo = rs.getString("account_number");
        int txnId = rs.getInt("transaction_id");
        double amount = rs.getDouble("amount");
        String reason = rs.getString("reason");
        RiskLevel risk = RiskLevel.valueOf(rs.getString("risk_level"));
        AlertStatus status = AlertStatus.valueOf(rs.getString("status"));
        LocalDateTime time = LocalDateTime.parse(rs.getString("timestamp"));
        return new FraudAlert(id, accNo, txnId, amount, reason, risk, status, time);
    }

    @Override
    public void save(FraudAlert alert) throws BankException {
        FraudAlert existing = findById(alert.getAlertId());
        if (existing == null) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO fraud_alerts (alert_id, account_number, transaction_id, amount, reason, risk_level, status, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, alert.getAlertId());
                ps.setString(2, alert.getAccountNumber());
                ps.setInt(3, alert.getTransactionId());
                ps.setDouble(4, alert.getAmount());
                ps.setString(5, alert.getReason());
                ps.setString(6, alert.getRiskLevel().name());
                ps.setString(7, alert.getStatus().name());
                ps.setString(8, alert.getTimestamp().toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new BankException(e.getMessage());
            }
        } else {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE fraud_alerts SET account_number = ?, transaction_id = ?, amount = ?, reason = ?, risk_level = ?, status = ?, timestamp = ? WHERE alert_id = ?")) {
                ps.setString(1, alert.getAccountNumber());
                ps.setInt(2, alert.getTransactionId());
                ps.setDouble(3, alert.getAmount());
                ps.setString(4, alert.getReason());
                ps.setString(5, alert.getRiskLevel().name());
                ps.setString(6, alert.getStatus().name());
                ps.setString(7, alert.getTimestamp().toString());
                ps.setInt(8, alert.getAlertId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new BankException(e.getMessage());
            }
        }
    }

    @Override
    public FraudAlert findById(int alertId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM fraud_alerts WHERE alert_id = ?")) {
            ps.setInt(1, alertId);
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
    public List<FraudAlert> findByAccountNumber(String accountNumber) {
        List<FraudAlert> list = new ArrayList<FraudAlert>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM fraud_alerts WHERE account_number = ?")) {
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
    public List<FraudAlert> findAll() {
        List<FraudAlert> list = new ArrayList<FraudAlert>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM fraud_alerts");
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
             PreparedStatement ps = conn.prepareStatement("SELECT MAX(alert_id) FROM fraud_alerts");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
        }
        return 1;
    }
}
