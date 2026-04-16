package com.novabank.repository;

import com.novabank.model.Transaction;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRepository {
    private final Map<Long, Transaction> transactions = new HashMap<>();

    private long nextId = 1L;

    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        transaction.setId(nextId++);
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public List<Transaction> searchByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank.");
        }
        return transactions.values().stream()
                .filter(t -> t.getAccount() != null && t.getAccount().getAccountNumber().equals(accountNumber))
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();
    }

    public List<Transaction> searchByAccountNumberAndDateRange(String accountNumber, LocalDate from, LocalDate to) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank.");
        }
        if (from == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        if (to == null) {
            throw new IllegalArgumentException("End date cannot be null.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        return transactions.values().stream()
                .filter(t -> t.getAccount() != null && t.getAccount().getAccountNumber().equals(accountNumber))
                .filter(t -> {
                    LocalDate date = t.getCreatedAt().toLocalDate();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .toList();
    }
}
