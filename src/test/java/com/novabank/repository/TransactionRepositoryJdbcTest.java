package com.novabank.repository;

import com.novabank.config.DatabaseConnectionManager;
import com.novabank.model.*;
import com.novabank.repository.jdbc.AccountRepositoryJdbc;
import com.novabank.repository.jdbc.ClientRepositoryJdbc;
import com.novabank.repository.jdbc.TransactionRepositoryJdbc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRepositoryJdbcTest {

    private final ClientRepository clientRepository = new ClientRepositoryJdbc();
    private final AccountRepository accountRepository = new AccountRepositoryJdbc();
    private final TransactionRepository transactionRepository = new TransactionRepositoryJdbc();

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
    @DisplayName("Saving a transaction should assign an ID and persist it")
    void save_newTransaction_shouldAssignIdAndPersist() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        Account account = accountRepository.save(new Account(client, "ES91210000000000000001"));

        Transaction saved = transactionRepository.save(
                TransactionFactory.createDeposit(account, new BigDecimal("100.00")));

        assertNotNull(saved.getId());
        assertEquals(TransactionType.DEPOSIT, saved.getType());
        assertEquals(0, new BigDecimal("100.00").compareTo(saved.getAmount()));
    }

    @Test
    @DisplayName("Searching by account number should return all transactions for that account")
    void searchByAccountNumber_shouldReturnTransactions() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        Account account = accountRepository.save(new Account(client, "ES91210000000000000001"));
        transactionRepository.save(TransactionFactory.createDeposit(account, new BigDecimal("200.00")));
        transactionRepository.save(TransactionFactory.createWithdrawal(account, new BigDecimal("50.00")));

        List<Transaction> transactions = transactionRepository.searchByAccountNumber("ES91210000000000000001");

        assertEquals(2, transactions.size());
    }

    @Test
    @DisplayName("Searching by account number should return transactions ordered by date descending")
    void searchByAccountNumber_shouldReturnTransactionsOrderedByDateDesc() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        Account account = accountRepository.save(new Account(client, "ES91210000000000000001"));
        transactionRepository.save(TransactionFactory.createDeposit(account, new BigDecimal("100.00")));
        transactionRepository.save(TransactionFactory.createWithdrawal(account, new BigDecimal("50.00")));

        List<Transaction> transactions = transactionRepository.searchByAccountNumber("ES91210000000000000001");

        assertFalse(transactions.get(0).getCreatedAt().isBefore(transactions.get(1).getCreatedAt()));
    }

    @Test
    @DisplayName("Searching by account number with no transactions should return empty list")
    void searchByAccountNumber_withNoTransactions_shouldReturnEmptyList() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        accountRepository.save(new Account(client, "ES91210000000000000001"));

        List<Transaction> transactions = transactionRepository.searchByAccountNumber("ES91210000000000000001");

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    @DisplayName("Searching by date range should return only transactions within the range")
    void searchByAccountNumberAndDateRange_shouldReturnTransactionsInRange() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        Account account = accountRepository.save(new Account(client, "ES91210000000000000001"));
        transactionRepository.save(TransactionFactory.createDeposit(account, new BigDecimal("100.00")));

        LocalDate today = LocalDate.now();
        List<Transaction> transactions = transactionRepository
                .searchByAccountNumberAndDateRange("ES91210000000000000001", today, today);

        assertEquals(1, transactions.size());
        assertEquals(TransactionType.DEPOSIT, transactions.get(0).getType());
    }

    @Test
    @DisplayName("Searching by date range with no matching transactions should return empty list")
    void searchByAccountNumberAndDateRange_withNoMatchingTransactions_shouldReturnEmptyList() {
        Client client = clientRepository.save(new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@test.com").phone("600000001")
                .build());
        Account account = accountRepository.save(new Account(client, "ES91210000000000000001"));
        transactionRepository.save(TransactionFactory.createDeposit(account, new BigDecimal("100.00")));

        LocalDate past = LocalDate.now().minusYears(1);
        List<Transaction> transactions = transactionRepository
                .searchByAccountNumberAndDateRange("ES91210000000000000001", past, past);

        assertTrue(transactions.isEmpty());
    }
}
