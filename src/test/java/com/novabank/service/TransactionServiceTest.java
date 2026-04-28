package com.novabank.service;

import com.novabank.config.ConnectionProvider;
import com.novabank.model.*;
import com.novabank.repository.AccountRepository;
import com.novabank.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock private AccountService accountService;
    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private ConnectionProvider connectionProvider;
    @Mock private Connection mockConnection;

    @InjectMocks
    private TransactionService transactionService;

    private static final String ACCOUNT_NUMBER_1 = "ES91210000000000000001";
    private static final String ACCOUNT_NUMBER_2 = "ES91210000000000000002";
    private static final String NON_EXISTENT_ACCOUNT = "ES00000000000000000000";

    private Account buildAccount(long id, String number, BigDecimal balance) {
        Client client = new ClientBuilder()
                .name("Test").surname("User")
                .dni("1234567" + id + "A").email("test" + id + "@email.com").phone("60000000" + id)
                .build();
        client.setId(id);
        Account account = new Account(client, number);
        account.setId(id);
        if (balance.compareTo(BigDecimal.ZERO) > 0) account.credit(balance);
        return account;
    }

    private void stubTransferConnection() throws Exception {
        when(connectionProvider.getConnection()).thenReturn(mockConnection);
    }

    // --- deposit ---

    @Test
    @DisplayName("Depositing a valid amount should increase the account balance")
    void deposit_withValidAmount_shouldIncreaseBalance() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        Account result = transactionService.deposit(ACCOUNT_NUMBER_1, new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("100.00").compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("Depositing a valid amount should save a DEPOSIT transaction")
    void deposit_withValidAmount_shouldRecordTransaction() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        transactionService.deposit(ACCOUNT_NUMBER_1, new BigDecimal("200.00"));

        verify(transactionRepository).save(argThat(t ->
                t.getType() == TransactionType.DEPOSIT &&
                t.getAmount().compareTo(new BigDecimal("200.00")) == 0));
    }

    @Test
    @DisplayName("Depositing a null amount should throw an exception")
    void deposit_withNullAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(ACCOUNT_NUMBER_1, null));
    }

    @Test
    @DisplayName("Depositing zero should throw an exception")
    void deposit_withZeroAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(ACCOUNT_NUMBER_1, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Depositing a negative amount should throw an exception")
    void deposit_withNegativeAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(ACCOUNT_NUMBER_1, new BigDecimal("-50.00")));
    }

    @Test
    @DisplayName("Depositing into a non-existent account should throw an exception")
    void deposit_withNonExistentAccount_shouldThrowException() {
        when(accountService.searchByAccountNumber(NON_EXISTENT_ACCOUNT))
                .thenThrow(new IllegalArgumentException("ERROR: Could not find an account."));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(NON_EXISTENT_ACCOUNT, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Depositing with a null account number should throw an exception")
    void deposit_withNullAccountNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(null, new BigDecimal("100.00")));
    }

    // --- withdrawal ---

    @Test
    @DisplayName("Withdrawing a valid amount should decrease the account balance")
    void withdrawal_withValidAmount_shouldDecreaseBalance() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("300.00"));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        Account result = transactionService.withdrawal(ACCOUNT_NUMBER_1, new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("200.00").compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("Withdrawing a valid amount should save a WITHDRAWAL transaction")
    void withdrawal_withValidAmount_shouldRecordTransaction() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("300.00"));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        transactionService.withdrawal(ACCOUNT_NUMBER_1, new BigDecimal("100.00"));

        verify(transactionRepository).save(argThat(t -> t.getType() == TransactionType.WITHDRAWAL));
    }

    @Test
    @DisplayName("Withdrawing the exact balance should leave the account at zero")
    void withdrawal_withExactBalance_shouldLeaveZeroBalance() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("100.00"));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        Account result = transactionService.withdrawal(ACCOUNT_NUMBER_1, new BigDecimal("100.00"));

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("Withdrawing more than the available balance should throw an exception")
    void withdrawal_withInsufficientBalance_shouldThrowException() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("50.00"));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(ACCOUNT_NUMBER_1, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Withdrawing from an empty account should throw an exception")
    void withdrawal_fromEmptyAccount_shouldThrowException() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(ACCOUNT_NUMBER_1, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Withdrawing a null amount should throw an exception")
    void withdrawal_withNullAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(ACCOUNT_NUMBER_1, null));
    }

    @Test
    @DisplayName("Withdrawing zero should throw an exception")
    void withdrawal_withZeroAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(ACCOUNT_NUMBER_1, BigDecimal.ZERO));
    }

    // --- transfer ---

    @Test
    @DisplayName("Transferring a valid amount should update both account balances correctly")
    void transfer_withValidAmount_shouldUpdateBothBalances() throws Exception {
        Account from = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("500.00"));
        Account to   = buildAccount(2L, ACCOUNT_NUMBER_2, BigDecimal.ZERO);
        stubTransferConnection();
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_1, mockConnection)).thenReturn(Optional.of(from));
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_2, mockConnection)).thenReturn(Optional.of(to));

        transactionService.transfer(ACCOUNT_NUMBER_1, ACCOUNT_NUMBER_2, new BigDecimal("200.00"));

        assertEquals(0, new BigDecimal("300.00").compareTo(from.getBalance()));
        assertEquals(0, new BigDecimal("200.00").compareTo(to.getBalance()));
    }

    @Test
    @DisplayName("Transferring should save OUTGOING and INCOMING transactions")
    void transfer_withValidAmount_shouldRecordBothTransactions() throws Exception {
        Account from = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("500.00"));
        Account to   = buildAccount(2L, ACCOUNT_NUMBER_2, BigDecimal.ZERO);
        stubTransferConnection();
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_1, mockConnection)).thenReturn(Optional.of(from));
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_2, mockConnection)).thenReturn(Optional.of(to));

        transactionService.transfer(ACCOUNT_NUMBER_1, ACCOUNT_NUMBER_2, new BigDecimal("200.00"));

        verify(transactionRepository).save(argThat(t -> t.getType() == TransactionType.OUTGOING_TRANSFER), eq(mockConnection));
        verify(transactionRepository).save(argThat(t -> t.getType() == TransactionType.INCOMING_TRANSFER), eq(mockConnection));
    }

    @Test
    @DisplayName("Transferring with insufficient balance should throw an exception")
    void transfer_withInsufficientBalance_shouldThrowException() throws Exception {
        Account from = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("50.00"));
        Account to   = buildAccount(2L, ACCOUNT_NUMBER_2, BigDecimal.ZERO);
        stubTransferConnection();
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_1, mockConnection)).thenReturn(Optional.of(from));
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_2, mockConnection)).thenReturn(Optional.of(to));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(ACCOUNT_NUMBER_1, ACCOUNT_NUMBER_2, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transferring to the same account should throw an exception")
    void transfer_toSameAccount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(ACCOUNT_NUMBER_1, ACCOUNT_NUMBER_1, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transferring a null amount should throw an exception")
    void transfer_withNullAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(ACCOUNT_NUMBER_1, ACCOUNT_NUMBER_2, null));
    }

    @Test
    @DisplayName("Transferring from a non-existent account should throw an exception")
    void transfer_fromNonExistentAccount_shouldThrowException() throws Exception {
        stubTransferConnection();
        when(accountRepository.searchByAccountNumber(NON_EXISTENT_ACCOUNT, mockConnection)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(NON_EXISTENT_ACCOUNT, ACCOUNT_NUMBER_2, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transferring to a non-existent account should throw an exception")
    void transfer_toNonExistentAccount_shouldThrowException() throws Exception {
        Account from = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("500.00"));
        stubTransferConnection();
        when(accountRepository.searchByAccountNumber(ACCOUNT_NUMBER_1, mockConnection)).thenReturn(Optional.of(from));
        when(accountRepository.searchByAccountNumber(NON_EXISTENT_ACCOUNT, mockConnection)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(ACCOUNT_NUMBER_1, NON_EXISTENT_ACCOUNT, new BigDecimal("100.00")));
    }

    // --- getBalance ---

    @Test
    @DisplayName("Getting balance should return the correct amount")
    void getBalance_shouldReturnCorrectBalance() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, new BigDecimal("350.00"));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);

        BigDecimal balance = transactionService.getBalance(ACCOUNT_NUMBER_1);

        assertEquals(0, new BigDecimal("350.00").compareTo(balance));
    }

    @Test
    @DisplayName("Getting balance for a non-existent account should throw an exception")
    void getBalance_withNonExistentAccount_shouldThrowException() {
        when(accountService.searchByAccountNumber(NON_EXISTENT_ACCOUNT))
                .thenThrow(new IllegalArgumentException("ERROR: Could not find an account."));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getBalance(NON_EXISTENT_ACCOUNT));
    }

    @Test
    @DisplayName("Getting balance with a null account number should throw an exception")
    void getBalance_withNullAccountNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getBalance(null));
    }

    // --- getHistory ---

    @Test
    @DisplayName("Getting history with no transactions should return an empty list")
    void getHistory_withNoTransactions_shouldReturnEmptyList() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);
        when(transactionRepository.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(List.of());

        List<Transaction> history = transactionService.getHistory(ACCOUNT_NUMBER_1);

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Getting history should return all transactions for the account")
    void getHistory_afterMultipleOperations_shouldReturnAllTransactions() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        List<Transaction> transactions = List.of(
                TransactionFactory.createDeposit(account, new BigDecimal("300.00")),
                TransactionFactory.createWithdrawal(account, new BigDecimal("100.00")));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);
        when(transactionRepository.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(transactions);

        List<Transaction> history = transactionService.getHistory(ACCOUNT_NUMBER_1);

        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("Getting history for a non-existent account should throw an exception")
    void getHistory_withNonExistentAccount_shouldThrowException() {
        when(accountService.searchByAccountNumber(NON_EXISTENT_ACCOUNT))
                .thenThrow(new IllegalArgumentException("ERROR: Could not find an account."));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistory(NON_EXISTENT_ACCOUNT));
    }

    // --- getHistoryByDate ---

    @Test
    @DisplayName("Getting history by date range should return transactions within the range")
    void getHistoryByDate_withTransactionsInRange_shouldReturnThem() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = List.of(
                TransactionFactory.createDeposit(account, new BigDecimal("100.00")));
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);
        when(transactionRepository.searchByAccountNumberAndDateRange(ACCOUNT_NUMBER_1, today, today))
                .thenReturn(transactions);

        List<Transaction> history = transactionService.getHistoryByDate(ACCOUNT_NUMBER_1, today, today);

        assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Getting history by date range with no matching transactions should return empty list")
    void getHistoryByDate_withNoTransactionsInRange_shouldReturnEmptyList() {
        Account account = buildAccount(1L, ACCOUNT_NUMBER_1, BigDecimal.ZERO);
        LocalDate past = LocalDate.now().minusYears(1);
        when(accountService.searchByAccountNumber(ACCOUNT_NUMBER_1)).thenReturn(account);
        when(transactionRepository.searchByAccountNumberAndDateRange(ACCOUNT_NUMBER_1, past, past))
                .thenReturn(List.of());

        List<Transaction> history = transactionService.getHistoryByDate(ACCOUNT_NUMBER_1, past, past);

        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Getting history by date range with null start date should throw an exception")
    void getHistoryByDate_withNullFromDate_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistoryByDate(ACCOUNT_NUMBER_1, null, LocalDate.now()));
    }

    @Test
    @DisplayName("Getting history by date range with null end date should throw an exception")
    void getHistoryByDate_withNullToDate_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistoryByDate(ACCOUNT_NUMBER_1, LocalDate.now(), null));
    }

    @Test
    @DisplayName("Getting history by date range for a non-existent account should throw an exception")
    void getHistoryByDate_withNonExistentAccount_shouldThrowException() {
        when(accountService.searchByAccountNumber(NON_EXISTENT_ACCOUNT))
                .thenThrow(new IllegalArgumentException("ERROR: Could not find an account."));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistoryByDate(NON_EXISTENT_ACCOUNT, LocalDate.now(), LocalDate.now()));
    }
}
