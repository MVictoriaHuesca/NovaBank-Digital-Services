package com.novabank.repository;

import com.novabank.model.Account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {
    private final Map<String, Account> accounts = new HashMap<>();

    private long nextId = 1L;

    public Account save(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        if(account.getId() == null) {
            account.setId(nextId++);
        }
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public Optional<Account> searchByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be null or blank.");
        }
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    public List<Account> searchByClientId(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null.");
        }
        return accounts.values().stream()
                .filter(a -> a.getClient() != null && a.getClient().getId().equals(clientId))
                .toList();
    }
}
