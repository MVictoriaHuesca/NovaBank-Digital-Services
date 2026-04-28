package com.novabank.repository.jdbc;

import com.novabank.config.DatabaseConnectionManager;
import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.repository.AccountRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryJdbc implements AccountRepository {

    private static final String SELECT_WITH_CLIENT =
            "SELECT a.id, a.account_number, a.balance, a.created_at, " +
            "c.id AS client_id, c.name, c.surname, c.dni, c.email, c.phone, c.created_at AS client_created_at " +
            "FROM accounts a JOIN clients c ON a.client_id = c.id ";

    @Override
    public Account save(Account account) {
        String sql = "INSERT INTO accounts (account_number, client_id, balance, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getAccountNumber());
            stmt.setLong(2, account.getClient().getId());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.setTimestamp(4, Timestamp.valueOf(account.getCreatedAt()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    account.setId(keys.getLong(1));
                }
            }
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving account", e);
        }
    }

    @Override
    public long countAccounts() {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting accounts", e);
        }
    }

    @Override
    public Optional<Account> searchByAccountNumber(String accountNumber) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            return searchByAccountNumber(accountNumber, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error searching account by number", e);
        }
    }

    @Override
    public Optional<Account> searchByAccountNumber(String accountNumber, Connection conn) {
        String sql = SELECT_WITH_CLIENT + "WHERE a.account_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapAccount(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching account by number", e);
        }
    }

    @Override
    public List<Account> searchByClientId(Long clientId) {
        String sql = SELECT_WITH_CLIENT + "WHERE a.client_id = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Account> accounts = new ArrayList<>();
                while (rs.next()) {
                    accounts.add(mapAccount(rs));
                }
                return accounts;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing accounts by client", e);
        }
    }

    @Override
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection()) {
            return updateBalance(accountId, newBalance, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating balance", e);
        }
    }

    @Override
    public Account updateBalance(Long accountId, BigDecimal newBalance, Connection conn) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setLong(2, accountId);
            stmt.executeUpdate();
            return searchById(accountId, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating balance", e);
        }
    }

    private Account searchById(Long accountId, Connection conn) throws SQLException {
        String sql = SELECT_WITH_CLIENT + "WHERE a.id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapAccount(rs);
                }
                throw new RuntimeException("Account not found after balance update: " + accountId);
            }
        }
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
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
        account.setId(rs.getLong("id"));
        account.credit(rs.getBigDecimal("balance"));
        account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return account;
    }
}
