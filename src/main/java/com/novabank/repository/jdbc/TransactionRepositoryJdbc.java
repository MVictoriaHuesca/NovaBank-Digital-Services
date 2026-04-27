package com.novabank.repository.jdbc;

import com.novabank.config.DatabaseConnectionManager;
import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.model.Transaction;
import com.novabank.model.TransactionType;
import com.novabank.repository.TransactionRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryJdbc implements TransactionRepository {

    private static final String SELECT_WITH_ACCOUNT =
            "SELECT t.id, t.type, t.amount, t.created_at, " +
            "a.id AS account_id, a.account_number, a.balance, a.created_at AS account_created_at, " +
            "c.id AS client_id, c.name, c.surname, c.dni, c.email, c.phone, c.created_at AS client_created_at " +
            "FROM transactions t " +
            "JOIN accounts a ON t.account_id = a.id " +
            "JOIN clients c ON a.client_id = c.id ";

    @Override
    public Transaction save(Transaction transaction) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            return save(transaction, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }

    @Override
    public Transaction save(Transaction transaction, Connection conn) {
        String sql = "INSERT INTO transactions (account_id, type, amount, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, transaction.getAccount().getId());
            stmt.setString(2, transaction.getType().name());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(transaction.getCreatedAt()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setId(keys.getLong(1));
                }
            }
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }

    @Override
    public List<Transaction> searchByAccountNumber(String accountNumber) {
        String sql = SELECT_WITH_ACCOUNT +
                "WHERE a.account_number = ? ORDER BY t.created_at DESC";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapTransactions(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching transactions by account number", e);
        }
    }

    @Override
    public List<Transaction> searchByAccountNumberAndDateRange(String accountNumber, LocalDate from, LocalDate to) {
        String sql = SELECT_WITH_ACCOUNT +
                "WHERE a.account_number = ? AND t.created_at >= ? AND t.created_at < ? ORDER BY t.created_at DESC";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setTimestamp(2, Timestamp.valueOf(from.atStartOfDay()));
            stmt.setTimestamp(3, Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapTransactions(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching transactions by date range", e);
        }
    }

    private List<Transaction> mapTransactions(ResultSet rs) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        while (rs.next()) {
            transactions.add(mapTransaction(rs));
        }
        return transactions;
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("dni"),
                rs.getString("email"),
                rs.getString("phone")
        );
        client.setId(rs.getLong("client_id"));
        client.setCreatedAt(rs.getTimestamp("client_created_at").toLocalDateTime());

        Account account = new Account(client, rs.getString("account_number"));
        account.setId(rs.getLong("account_id"));
        account.credit(rs.getBigDecimal("balance"));
        account.setCreatedAt(rs.getTimestamp("account_created_at").toLocalDateTime());

        Transaction transaction = new Transaction(account,
                TransactionType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"));
        transaction.setId(rs.getLong("id"));
        transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return transaction;
    }
}
