package com.novabank.repository;

import com.novabank.model.Transaction;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> searchByAccountNumber(String accountNumber);
    List<Transaction> searchByAccountNumberAndDateRange(String accountNumber, LocalDate from, LocalDate to);

    Transaction save(Transaction transaction, Connection conn);
}
