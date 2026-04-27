package com.novabank.repository.jdbc;

import com.novabank.config.DatabaseConnectionManager;
import com.novabank.model.Client;
import com.novabank.repository.ClientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepositoryJdbc implements ClientRepository {

    @Override
    public Client save(Client client) {
        if (client.getId() == null) {
            return insert(client);
        }
        return update(client);
    }

    private Client insert(Client client) {
        String sql = "INSERT INTO clients (name, surname, dni, email, phone, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getSurname());
            stmt.setString(3, client.getDni());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getPhone());
            stmt.setTimestamp(6, Timestamp.valueOf(client.getCreatedAt()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    client.setId(keys.getLong(1));
                }
            }
            return client;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting client", e);
        }
    }

    private Client update(Client client) {
        String sql = "UPDATE clients SET name = ?, surname = ?, dni = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getSurname());
            stmt.setString(3, client.getDni());
            stmt.setString(4, client.getEmail());
            stmt.setString(5, client.getPhone());
            stmt.setLong(6, client.getId());
            stmt.executeUpdate();
            return client;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating client", e);
        }
    }

    @Override
    public Optional<Client> searchById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapClient(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching client by id", e);
        }
    }

    @Override
    public Optional<Client> searchByDni(String dni) {
        String sql = "SELECT * FROM clients WHERE dni = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapClient(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching client by dni", e);
        }
    }

    @Override
    public List<Client> searchAll() {
        String sql = "SELECT * FROM clients ORDER BY id";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Client> clients = new ArrayList<>();
            while (rs.next()) {
                clients.add(mapClient(rs));
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException("Error listing clients", e);
        }
    }

    @Override
    public boolean existsByDni(String dni) {
        return existsByField("dni", dni);
    }

    @Override
    public boolean existsByEmail(String email) {
        return existsByField("email", email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return existsByField("phone", phone);
    }

    private boolean existsByField(String field, String value) {
        String sql = "SELECT COUNT(*) FROM clients WHERE " + field + " = ?";
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking " + field + " existence", e);
        }
    }

    private Client mapClient(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("dni"),
                rs.getString("email"),
                rs.getString("phone")
        );
        client.setId(rs.getLong("id"));
        client.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return client;
    }
}
