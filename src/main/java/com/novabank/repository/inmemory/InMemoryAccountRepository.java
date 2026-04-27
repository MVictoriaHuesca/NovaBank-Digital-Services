package com.novabank.repository.inmemory;

import com.novabank.model.Account;
import com.novabank.repository.AccountRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();
    private long nextId = 1L;

    @Override
    public Account save(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        if (account.getId() == null) {
            account.setId(nextId++);
        }
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public Optional<Account> searchByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank.");
        }
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    @Override
    public List<Account> searchByClientId(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null.");
        }
        return accounts.values().stream()
                .filter(a -> a.getClient() != null && a.getClient().getId().equals(clientId))
                .toList();
    }

    @Override
    public Account updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accounts.values().stream()
                .filter(a -> a.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        BigDecimal diff = newBalance.subtract(account.getBalance());
        if (diff.compareTo(BigDecimal.ZERO) >= 0) {
            account.credit(diff);
        } else {
            account.debit(diff.abs());
        }
        return account;
    }

    @Override
    public Optional<Account> searchByAccountNumber(String accountNumber, Connection conn) {
        return searchByAccountNumber(accountNumber);
    }

    @Override
    public Account updateBalance(Long accountId, BigDecimal newBalance, Connection conn) {
        return updateBalance(accountId, newBalance);
    }
}
