package com.novabank.service;

import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.model.Transaction;
import com.novabank.model.TransactionType;
import com.novabank.repository.AccountRepository;
import com.novabank.repository.ClientRepository;
import com.novabank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {
    private ClientService clientService;
    private AccountService accountService;
    private TransactionService transactionService;

    private static final String NON_EXISTENT_ACCOUNT = "ES00000000000000000000";

    @BeforeEach
    void setUp() {
        clientService = new ClientService(new ClientRepository());
        accountService = new AccountService(new AccountRepository(), clientService);
        transactionService = new TransactionService(accountService, new TransactionRepository());
    }

    private Account createAccount() {
        Client client = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");
        return accountService.createAccount(client.getId());
    }

    private Account createSecondAccount() {
        Client client = clientService.save("Ana", "García", "87654321B", "ana@email.com", "600654321");
        return accountService.createAccount(client.getId());
    }

    @Test
    @DisplayName("Depositing a valid amount should increase the account balance")
    void deposit_withValidAmount_shouldIncreaseBalance() {
        Account account = createAccount();

        Account result = transactionService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("100.00").compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("Depositing a valid amount should record a DEPOSIT transaction")
    void deposit_withValidAmount_shouldRecordTransaction() {
        Account account = createAccount();

        transactionService.deposit(account.getAccountNumber(), new BigDecimal("200.00"));
        List<Transaction> history = transactionService.getHistory(account.getAccountNumber());

        assertEquals(1, history.size());
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(0, new BigDecimal("200.00").compareTo(history.get(0).getAmount()));
    }

    @Test
    @DisplayName("Depositing a null amount should throw an exception")
    void deposit_withNullAmount_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(account.getAccountNumber(), null));
    }

    @Test
    @DisplayName("Depositing zero should throw an exception")
    void deposit_withZeroAmount_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(account.getAccountNumber(), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Depositing a negative amount should throw an exception")
    void deposit_withNegativeAmount_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(account.getAccountNumber(), new BigDecimal("-50.00")));
    }

    @Test
    @DisplayName("Depositing into a non-existent account should throw an exception")
    void deposit_withNonExistentAccount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(NON_EXISTENT_ACCOUNT, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Depositing with a null account number should throw an exception")
    void deposit_withNullAccountNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.deposit(null, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Withdrawing a valid amount should decrease the account balance")
    void withdrawal_withValidAmount_shouldDecreaseBalance() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("300.00"));

        Account result = transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("200.00").compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("Withdrawing a valid amount should record a WITHDRAWAL transaction")
    void withdrawal_withValidAmount_shouldRecordTransaction() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("300.00"));

        transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("100.00"));
        List<Transaction> history = transactionService.getHistory(account.getAccountNumber());

        assertTrue(history.stream().anyMatch(t -> t.getType() == TransactionType.WITHDRAWAL));
    }

    @Test
    @DisplayName("Withdrawing the exact balance should leave the account at zero")
    void withdrawal_withExactBalance_shouldLeaveZeroBalance() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));

        Account result = transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("100.00"));

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("Withdrawing more than the available balance should throw an exception")
    void withdrawal_withInsufficientBalance_shouldThrowException() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("50.00"));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Withdrawing from an empty account should throw an exception")
    void withdrawal_fromEmptyAccount_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Withdrawing a null amount should throw an exception")
    void withdrawal_withNullAmount_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(account.getAccountNumber(), null));
    }

    @Test
    @DisplayName("Withdrawing zero should throw an exception")
    void withdrawal_withZeroAmount_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.withdrawal(account.getAccountNumber(), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Transferring a valid amount should update both account balances correctly")
    void transfer_withValidAmount_shouldUpdateBothBalances() {
        Account from = createAccount();
        Account to = createSecondAccount();
        transactionService.deposit(from.getAccountNumber(), new BigDecimal("500.00"));

        transactionService.transfer(from.getAccountNumber(), to.getAccountNumber(), new BigDecimal("200.00"));

        assertEquals(0, new BigDecimal("300.00").compareTo(from.getBalance()));
        assertEquals(0, new BigDecimal("200.00").compareTo(to.getBalance()));
    }

    @Test
    @DisplayName("Transferring should record OUTGOING and INCOMING transactions")
    void transfer_withValidAmount_shouldRecordBothTransactions() {
        Account from = createAccount();
        Account to = createSecondAccount();
        transactionService.deposit(from.getAccountNumber(), new BigDecimal("500.00"));

        transactionService.transfer(from.getAccountNumber(), to.getAccountNumber(), new BigDecimal("200.00"));

        List<Transaction> fromHistory = transactionService.getHistory(from.getAccountNumber());
        List<Transaction> toHistory = transactionService.getHistory(to.getAccountNumber());

        assertTrue(fromHistory.stream().anyMatch(t -> t.getType() == TransactionType.OUTGOING_TRANSFER));
        assertTrue(toHistory.stream().anyMatch(t -> t.getType() == TransactionType.INCOMING_TRANSFER));
    }

    @Test
    @DisplayName("Transferring with insufficient balance should throw an exception")
    void transfer_withInsufficientBalance_shouldThrowException() {
        Account from = createAccount();
        Account to = createSecondAccount();
        transactionService.deposit(from.getAccountNumber(), new BigDecimal("50.00"));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(from.getAccountNumber(), to.getAccountNumber(), new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transferring to the same account should throw an exception")
    void transfer_toSameAccount_shouldThrowException() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("500.00"));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(account.getAccountNumber(), account.getAccountNumber(), new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transferring a null amount should throw an exception")
    void transfer_withNullAmount_shouldThrowException() {
        Account from = createAccount();
        Account to = createSecondAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(from.getAccountNumber(), to.getAccountNumber(), null));
    }

    @Test
    @DisplayName("Transferring from a non-existent account should throw an exception")
    void transfer_fromNonExistentAccount_shouldThrowException() {
        Account to = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(NON_EXISTENT_ACCOUNT, to.getAccountNumber(), new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Transferring to a non-existent account should throw an exception")
    void transfer_toNonExistentAccount_shouldThrowException() {
        Account from = createAccount();
        transactionService.deposit(from.getAccountNumber(), new BigDecimal("500.00"));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(from.getAccountNumber(), NON_EXISTENT_ACCOUNT, new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Getting balance should return the correct amount after operations")
    void getBalance_afterDepositAndWithdrawal_shouldReturnCorrectBalance() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("500.00"));
        transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("150.00"));

        BigDecimal balance = transactionService.getBalance(account.getAccountNumber());

        assertEquals(0, new BigDecimal("350.00").compareTo(balance));
    }

    @Test
    @DisplayName("Getting balance for a non-existent account should throw an exception")
    void getBalance_withNonExistentAccount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getBalance(NON_EXISTENT_ACCOUNT));
    }

    @Test
    @DisplayName("Getting balance with a null account number should throw an exception")
    void getBalance_withNullAccountNumber_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getBalance(null));
    }

    @Test
    @DisplayName("Getting history with no transactions should return an empty list")
    void getHistory_withNoTransactions_shouldReturnEmptyList() {
        Account account = createAccount();

        List<Transaction> history = transactionService.getHistory(account.getAccountNumber());

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Getting history should return all transactions for the account")
    void getHistory_afterMultipleOperations_shouldReturnAllTransactions() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("300.00"));
        transactionService.withdrawal(account.getAccountNumber(), new BigDecimal("100.00"));

        List<Transaction> history = transactionService.getHistory(account.getAccountNumber());

        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("Getting history for a non-existent account should throw an exception")
    void getHistory_withNonExistentAccount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistory(NON_EXISTENT_ACCOUNT));
    }

    @Test
    @DisplayName("Getting history by date range should return transactions within the range")
    void getHistoryByDate_withTransactionsInRange_shouldReturnThem() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));

        LocalDate today = LocalDate.now();
        List<Transaction> history = transactionService.getHistoryByDate(
                account.getAccountNumber(), today, today);

        assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Getting history by date range with no matching transactions should return empty list")
    void getHistoryByDate_withNoTransactionsInRange_shouldReturnEmptyList() {
        Account account = createAccount();
        transactionService.deposit(account.getAccountNumber(), new BigDecimal("100.00"));

        LocalDate past = LocalDate.now().minusYears(1);
        List<Transaction> history = transactionService.getHistoryByDate(
                account.getAccountNumber(), past, past);

        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Getting history by date range with null start date should throw an exception")
    void getHistoryByDate_withNullFromDate_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistoryByDate(account.getAccountNumber(), null, LocalDate.now()));
    }

    @Test
    @DisplayName("Getting history by date range with null end date should throw an exception")
    void getHistoryByDate_withNullToDate_shouldThrowException() {
        Account account = createAccount();

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistoryByDate(account.getAccountNumber(), LocalDate.now(), null));
    }

    @Test
    @DisplayName("Getting history by date range for a non-existent account should throw an exception")
    void getHistoryByDate_withNonExistentAccount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getHistoryByDate(NON_EXISTENT_ACCOUNT, LocalDate.now(), LocalDate.now()));
    }
}
