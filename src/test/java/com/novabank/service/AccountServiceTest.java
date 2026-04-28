package com.novabank.service;

import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.model.ClientBuilder;
import com.novabank.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AccountService accountService;

    private Client buildClient(long id) {
        Client c = new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni("12345678A").email("juan@email.com").phone("600123456")
                .build();
        c.setId(id);
        return c;
    }

    private void stubSaveAccount() {
        long[] idSeq = {1L};
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(idSeq[0]++);
            return a;
        });
    }

    @Test
    @DisplayName("Creating an account for an existing customer must return an account with IBAN and zero balance")
    void createAccount_withExistingClient_shouldReturnValidAccount() {
        Client client = buildClient(1L);
        when(clientService.searchById(1L)).thenReturn(client);
        stubSaveAccount();

        Account account = accountService.createAccount(1L);

        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertTrue(account.getAccountNumber().startsWith("ES"));
        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(1L, account.getClient().getId());
    }

    @Test
    @DisplayName("Creating two accounts for the same client must generate different IBAN numbers")
    void createAccount_twoAccounts_mustHaveDifferentNumbers() {
        Client client = buildClient(1L);
        when(clientService.searchById(1L)).thenReturn(client);
        stubSaveAccount();

        Account a1 = accountService.createAccount(1L);
        Account a2 = accountService.createAccount(1L);

        assertNotEquals(a1.getAccountNumber(), a2.getAccountNumber());
    }

    @Test
    @DisplayName("Listing customer accounts should return only their accounts")
    void listCustomerAccounts_withTwoAccounts_shouldReturnList() {
        Client client = buildClient(1L);
        Account a1 = new Account(client, "ES91210000000000000001");
        a1.setId(1L);
        Account a2 = new Account(client, "ES91210000000000000002");
        a2.setId(2L);
        when(clientService.searchById(1L)).thenReturn(client);
        when(accountRepository.searchByClientId(1L)).thenReturn(List.of(a1, a2));

        List<Account> accounts = accountService.listClientAccounts(1L);

        assertEquals(2, accounts.size());
        accounts.forEach(a -> assertEquals(1L, a.getClient().getId()));
    }

    @Test
    @DisplayName("Creating an account for a non-existent client should throw an exception")
    void createAccount_withNonExistentClient_shouldThrowException() {
        when(clientService.searchById(9999L))
                .thenThrow(new IllegalArgumentException("ERROR: Could not find a client with ID 9999."));

        assertThrows(IllegalArgumentException.class, () ->
                accountService.createAccount(9999L));
    }

    @Test
    @DisplayName("Searching for an account by a non-existent number should throw an exception")
    void searchByNumber_withNonexistentNumber_shouldThrowException() {
        when(accountRepository.searchByAccountNumber("ES00000000000000000000"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                accountService.searchByAccountNumber("ES00000000000000000000"));
    }

    @Test
    @DisplayName("Searching by null account number should throw an exception")
    void searchByNumber_withNullNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.searchByAccountNumber(null));
    }

    @Test
    @DisplayName("Searching by blank account number should throw an exception")
    void searchByNumber_withBlankNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.searchByAccountNumber("   "));
    }

    @Test
    @DisplayName("Creating an account with null client ID should throw an exception")
    void createAccount_withNullClientId_shouldThrowException() {
        when(clientService.searchById(null))
                .thenThrow(new IllegalArgumentException("ERROR: ID cannot be null."));

        assertThrows(IllegalArgumentException.class, () ->
                accountService.createAccount(null));
    }

    @Test
    @DisplayName("Listing accounts for a client with no accounts should return an empty list")
    void listClientAccounts_withNoAccounts_shouldReturnEmptyList() {
        Client client = buildClient(1L);
        when(clientService.searchById(1L)).thenReturn(client);
        when(accountRepository.searchByClientId(1L)).thenReturn(List.of());

        List<Account> accounts = accountService.listClientAccounts(1L);

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    @DisplayName("Listing accounts for a non-existent client should throw an exception")
    void listClientAccounts_withNonExistentClient_shouldThrowException() {
        when(clientService.searchById(9999L))
                .thenThrow(new IllegalArgumentException("ERROR: Could not find a client with ID 9999."));

        assertThrows(IllegalArgumentException.class, () ->
                accountService.listClientAccounts(9999L));
    }
}
