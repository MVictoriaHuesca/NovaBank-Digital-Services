package com.novabank.repository;

import com.novabank.config.DatabaseConnectionManager;
import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.model.ClientBuilder;
import com.novabank.repository.jdbc.AccountRepositoryJdbc;
import com.novabank.repository.jdbc.ClientRepositoryJdbc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AccountRepositoryJdbcTest {

    private final ClientRepository clientRepository = new ClientRepositoryJdbc();
    private final AccountRepository accountRepository = new AccountRepositoryJdbc();

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
    @DisplayName("Saving a new account should assign an ID and persist all fields")
    void save_newAccount_shouldAssignIdAndPersist() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());

        Account saved = accountRepository.save(new Account(client, "ES91210000000000000001"));

        assertNotNull(saved.getId());
        assertEquals("ES91210000000000000001", saved.getAccountNumber());
        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getBalance()));
        assertEquals(client.getId(), saved.getClient().getId());
    }

    @Test
    @DisplayName("Searching by account number should return the correct account")
    void searchByAccountNumber_withExistingNumber_shouldReturnAccount() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        accountRepository.save(new Account(client, "ES91210000000000000001"));

        Optional<Account> found = accountRepository.searchByAccountNumber("ES91210000000000000001");

        assertTrue(found.isPresent());
        assertEquals("ES91210000000000000001", found.get().getAccountNumber());
    }

    @Test
    @DisplayName("Searching by non-existent account number should return empty")
    void searchByAccountNumber_withNonExistentNumber_shouldReturnEmpty() {
        Optional<Account> found = accountRepository.searchByAccountNumber("ES00000000000000000000");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Searching by client ID should return all accounts for that client")
    void searchByClientId_shouldReturnClientAccounts() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        accountRepository.save(new Account(client, "ES91210000000000000001"));
        accountRepository.save(new Account(client, "ES91210000000000000002"));

        List<Account> accounts = accountRepository.searchByClientId(client.getId());

        assertEquals(2, accounts.size());
        accounts.forEach(a -> assertEquals(client.getId(), a.getClient().getId()));
    }

    @Test
    @DisplayName("Updating balance should persist the new balance")
    void updateBalance_shouldPersistNewBalance() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        Account saved = accountRepository.save(new Account(client, "ES91210000000000000001"));

        accountRepository.updateBalance(saved.getId(), new BigDecimal("500.00"));

        Optional<Account> updated = accountRepository.searchByAccountNumber("ES91210000000000000001");
        assertTrue(updated.isPresent());
        assertEquals(0, new BigDecimal("500.00").compareTo(updated.get().getBalance()));
    }

    @Test
    @DisplayName("countAccounts should return the correct number of accounts")
    void countAccounts_shouldReturnCorrectCount() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        accountRepository.save(new Account(client, "ES91210000000000000001"));
        accountRepository.save(new Account(client, "ES91210000000000000002"));

        assertEquals(2, accountRepository.countAccounts());
    }

    @Test
    @DisplayName("Searching by client ID with no accounts should return an empty list")
    void searchByClientId_withNoAccounts_shouldReturnEmptyList() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());

        List<Account> accounts = accountRepository.searchByClientId(client.getId());

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }
}
