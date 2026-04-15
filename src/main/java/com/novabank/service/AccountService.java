package com.novabank.service;

import com.novabank.model.Account;
import com.novabank.model.Client;
import com.novabank.repository.AccountRepository;

import java.util.List;

public class AccountService {
    private long nextAccountNumber = 1L;

    private final AccountRepository accountRepository;
    private final ClientService clientService;


    public AccountService(AccountRepository accountRepository, ClientService clientService) {
        this.accountRepository = accountRepository;
        this.clientService = clientService;
    }

    public Account createAccount(Long clientId) {
        Client client = clientService.searchById(clientId);
        String numberAccount = generateAccountNumber();
        Account account = new Account(client, numberAccount);
        return accountRepository.save(account);
    }

    public List<Account> listClientAccounts(Long clientId) {
        clientService.searchById(clientId);
        return accountRepository.searchByClientId(clientId);
    }

    public Account searchByNumberAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank())
            throw new IllegalArgumentException("ERROR: Account number cannot be null or blank.");
        return accountRepository.searchByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ERROR: Could not find an account with number " + accountNumber + "."));
    }

    private String generateAccountNumber() {
        return String.format("ES91210000%012d", nextAccountNumber++);
    }
}
