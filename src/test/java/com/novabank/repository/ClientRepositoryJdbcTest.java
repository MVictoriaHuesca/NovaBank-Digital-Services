package com.novabank.repository;

import com.novabank.config.DatabaseConnectionManager;
import com.novabank.model.Client;
import com.novabank.model.ClientBuilder;
import com.novabank.repository.jdbc.ClientRepositoryJdbc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryJdbcTest {

    private final ClientRepository repository = new ClientRepositoryJdbc();

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection conn = DatabaseConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM transactions");
            stmt.execute("DELETE FROM accounts");
            stmt.execute("DELETE FROM clients");
        }
    }

    @Test
    @DisplayName("Saving a new client should assign an ID and persist all fields")
    void save_newClient_shouldAssignIdAndPersist() {
        Client client = new ClientBuilder()
                .name("Ana").surname("García")
                .dni("12345678A").email("ana@test.com").phone("600000001")
                .build();

        Client saved = repository.save(client);

        assertNotNull(saved.getId());
        assertEquals("Ana", saved.getName());
        assertEquals("García", saved.getSurname());
        assertEquals("12345678A", saved.getDni());
        assertEquals("ana@test.com", saved.getEmail());
        assertEquals("600000001", saved.getPhone());
    }

    @Test
    @DisplayName("Searching by ID should return the saved client")
    void searchById_withExistingId_shouldReturnClient() {
        Client saved = repository.save(new ClientBuilder()
                .name("Ana").surname("García")
                .dni("12345678A").email("ana@test.com").phone("600000001")
                .build());

        Optional<Client> found = repository.searchById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("12345678A", found.get().getDni());
    }

    @Test
    @DisplayName("Searching by non-existent ID should return empty")
    void searchById_withNonExistentId_shouldReturnEmpty() {
        Optional<Client> found = repository.searchById(99999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Searching by DNI should return the correct client")
    void searchByDni_withExistingDni_shouldReturnClient() {
        repository.save(new ClientBuilder()
                .name("Ana").surname("García")
                .dni("12345678A").email("ana@test.com").phone("600000001")
                .build());

        Optional<Client> found = repository.searchByDni("12345678A");

        assertTrue(found.isPresent());
        assertEquals("12345678A", found.get().getDni());
    }

    @Test
    @DisplayName("Searching by non-existent DNI should return empty")
    void searchByDni_withNonExistentDni_shouldReturnEmpty() {
        Optional<Client> found = repository.searchByDni("99999999Z");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("searchAll should return all saved clients")
    void searchAll_shouldReturnAllSavedClients() {
        repository.save(new ClientBuilder()
                .name("Ana").surname("García")
                .dni("11111111A").email("ana@test.com").phone("600000001")
                .build());
        repository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("22222222B").email("juan@test.com").phone("600000002")
                .build());

        List<Client> clients = repository.searchAll();

        assertEquals(2, clients.size());
    }

    @Test
    @DisplayName("existsByDni should return true when DNI exists")
    void existsByDni_withExistingDni_shouldReturnTrue() {
        repository.save(new ClientBuilder()
                .name("Ana").surname("García")
                .dni("12345678A").email("ana@test.com").phone("600000001")
                .build());

        assertTrue(repository.existsByDni("12345678A"));
    }

    @Test
    @DisplayName("existsByDni should return false when DNI does not exist")
    void existsByDni_withNonExistentDni_shouldReturnFalse() {
        assertFalse(repository.existsByDni("99999999Z"));
    }

    @Test
    @DisplayName("existsByEmail should return true when email exists")
    void existsByEmail_withExistingEmail_shouldReturnTrue() {
        repository.save(new ClientBuilder()
                .name("Ana").surname("García")
                .dni("12345678A").email("ana@test.com").phone("600000001")
                .build());

        assertTrue(repository.existsByEmail("ana@test.com"));
    }

    @Test
    @DisplayName("existsByPhone should return true when phone exists")
    void existsByPhone_withExistingPhone_shouldReturnTrue() {
        repository.save(new ClientBuilder()
                .name("Ana").surname("García")
                .dni("12345678A").email("ana@test.com").phone("600000001")
                .build());

        assertTrue(repository.existsByPhone("600000001"));
    }
}
