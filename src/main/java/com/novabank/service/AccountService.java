package com.novabank.service;

import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientService clientService;
    private long nextAccountNumber;

    public AccountService(AccountRepository accountRepository, ClientService clientService) {
        this.accountRepository = accountRepository;
        this.clientService = clientService;
        this.nextAccountNumber = accountRepository.countAccounts() + 1L;
    }

    public Account createAccount(Long clientId) {
        Client client = clientService.searchById(clientId);
        String accountNumber = generateAccountNumber();
        Account account = new Account(client, accountNumber);
        return accountRepository.save(account);
    }

    public List<Account> listClientAccounts(Long clientId) {
        clientService.searchById(clientId);
        return accountRepository.searchByClientId(clientId);
    }

    public Account searchByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank())
            throw new IllegalArgumentException("ERROR: Account number cannot be null or blank.");
        return accountRepository.searchByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: Could not find an account with number " + accountNumber + "."));
    }

    public void updateBalance(Long accountId, BigDecimal newBalance) {
        // TODO #next-issue: wrap in a single JDBC transaction for atomicity
        accountRepository.updateBalance(accountId, newBalance);
    }

    private String generateAccountNumber() {
        return String.format("ES91210000%012d", nextAccountNumber++);
    }
}
