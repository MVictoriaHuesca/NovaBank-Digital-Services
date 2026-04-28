package com.novabank.repository;

import com.novabank.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> searchByAccountNumber(String accountNumber);
    List<Account> searchByClientId(Long clientId);
    Account updateBalance(Long accountId, BigDecimal newBalance);

    long countAccounts();

    Optional<Account> searchByAccountNumber(String accountNumber, Connection conn);
    Account updateBalance(Long accountId, BigDecimal newBalance, Connection conn);
}
