package com.novabank.service;

import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.repository.AccountRepository;
import com.novabank.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {
    private AccountRepository accountRepository;
    private ClientRepository clientRepository;
    private ClientService clientService;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        clientRepository = new ClientRepository();
        clientService = new ClientService(clientRepository);
        accountRepository = new AccountRepository();
        accountService = new AccountService(accountRepository, clientService);
    }

    @Test
    @DisplayName("Creating an account for an existing customer must return an account with IBAN and zero balance")
    void createAccount_withExistingClient_shouldReturnValidAccount() {
        // Arrange
        Client client = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");

        // Act
        Account account = accountService.createAccount(client.getId());

        // Assert
        assertNotNull(account);
        assertNotNull(account.getNumberAccount());
        assertTrue(account.getNumberAccount().startsWith("ES"));
        assertEquals(0, account.getBalance().compareTo(java.math.BigDecimal.ZERO));
        assertEquals(client.getId(), account.getClient().getId());
    }

    @Test
    @DisplayName("Creating two accounts for the same client must generate different IBAN numbers")
    void createAccount_twoAccounts_mustHaveDifferentNumbers() {
        // Arrange
        Client client = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");

        // Act
        Account c1 = accountService.createAccount(client.getId());
        Account c2 = accountService.createAccount(client.getId());

        // Assert
        assertNotEquals(c1.getNumberAccount(), c2.getNumberAccount());
    }

    @Test
    @DisplayName("Listing customer accounts should return only their accounts")
    void listCustomerAccounts_withTwoAccounts_shouldReturnList() {
        // Arrange
        Client client = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");
        accountService.createAccount(client.getId());
        accountService.createAccount(client.getId());

        // Act
        List<Account> accounts = accountService.listClientAccounts(client.getId());

        // Assert
        assertEquals(2, accounts.size());
        accounts.forEach(a -> assertEquals(client.getId(), a.getClient().getId()));
    }

    @Test
    @DisplayName("Creating an account for a non-existent client should throw an exception")
    void createAccount_withNonExistentClient_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.createAccount(9999L));
    }

    @Test
    @DisplayName("Searching for an account by a non-existent number should throw an exception")
    void searchByNumber_withNonexistentNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.searchByNumberAccount("ES00000000000000000000"));
    }

    @Test
    @DisplayName("Searching by null account number should throw an exception")
    void searchByNumber_withNullNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.searchByNumberAccount(null));
    }

    @Test
    @DisplayName("Searching by blank account number should throw an exception")
    void searchByNumber_withBlankNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.searchByNumberAccount("   "));
    }

    @Test
    @DisplayName("Creating an account with null client ID should throw an exception")
    void createAccount_withNullClientId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.createAccount(null));
    }

    @Test
    @DisplayName("Listing accounts for a client with no accounts should return an empty list")
    void listClientAccounts_withNoAccounts_shouldReturnEmptyList() {
        Client client = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");

        List<Account> accounts = accountService.listClientAccounts(client.getId());

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    @DisplayName("Listing accounts for a non-existent client should throw an exception")
    void listClientAccounts_withNonExistentClient_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.listClientAccounts(9999L));
    }
}
