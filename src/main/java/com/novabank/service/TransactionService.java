package com.novabank.service;

import com.novabank.model.Account;
import com.novabank.model.Transaction;
import com.novabank.model.TransactionType;
import com.novabank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountService accountService, TransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }

    public Account deposit(String accountNumber, BigDecimal amount) {
        validatePositiveAmount(amount);

        Account account = searchAccountOrThrow(accountNumber);

        account.credit(amount);

        Transaction transaction = new Transaction(account, TransactionType.DEPOSIT, amount);
        transactionRepository.save(transaction);

        return account;
    }

    public Account withdrawal(String accountNumber, BigDecimal amount) {
        validatePositiveAmount(amount);
        Account account = searchAccountOrThrow(accountNumber);
        validateEnoughBalance(account, amount);

        account.debit(amount);

        Transaction transaction = new Transaction(account, TransactionType.WITHDRAWAL, amount);
        transactionRepository.save(transaction);

        return account;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        validatePositiveAmount(amount);

        Account from = searchAccountOrThrow(fromAccountNumber);
        Account to = searchAccountOrThrow(toAccountNumber);
        validateDifferentAccounts(fromAccountNumber, toAccountNumber);
        validateEnoughBalance(from, amount);

        from.debit(amount);

        try {
            to.credit(amount);

            Transaction fromTransaction = new Transaction(from, TransactionType.OUTGOING_TRANSFER, amount);
            transactionRepository.save(fromTransaction);

            Transaction toTransaction = new Transaction(to, TransactionType.INCOMING_TRANSFER, amount);
            transactionRepository.save(toTransaction);

        } catch(RuntimeException e) {
            from.credit(amount);
            throw e;
        }
    }

    public BigDecimal getBalance(String accountNumber) {
        return searchAccountOrThrow(accountNumber).getBalance();
    }

    public List<Transaction> getHistory(String accountNumber) {
        searchAccountOrThrow(accountNumber);
        return transactionRepository.searchByAccountNumber(accountNumber);
    }

    public List<Transaction> getHistoryByDate(String accountNumber, LocalDate from, LocalDate to) {
        if (from == null) {
            throw new IllegalArgumentException("ERROR: Start date cannot be null.");
        }
        if (to == null) {
            throw new IllegalArgumentException("ERROR: End date cannot be null.");
        }
        searchAccountOrThrow(accountNumber);
        return transactionRepository.searchByAccountNumberAndDateRange(accountNumber, from, to);
    }

    private void validateEnoughBalance(Account account, BigDecimal amount) {
        if(account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    String.format("ERROR: Insufficient balance. Available balance: %.2f €. Requested amount: %.2f €.",
                            account.getBalance(), amount));
        }
    }

    private void validateDifferentAccounts(String fromAccountNumber, String toAccountNumber) {
        if(fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("ERROR: Source and destination accounts cannot be the same.");
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("ERROR: Amount must be a positive value.");
        }
    }

    private Account searchAccountOrThrow(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("ERROR: Account number cannot be null or blank.");
        }
        return accountService.searchByAccountNumber(accountNumber);
    }
}
