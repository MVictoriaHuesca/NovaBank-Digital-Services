package com.novabank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private Long id;
    private String accountNumber;
    private Client client;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public Account(Client client, String accountNumber) {
        this.client = client;
        this.accountNumber = accountNumber;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Client getClient() {
        return client;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) { this.id = id; }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAccountNumber(String numberAccount) {
        this.accountNumber = accountNumber;
    }


    @Override
    public String toString(){
        return String.format("Account{numberAccount='%s', client='%s', balance=%s}",
                accountNumber,
                client != null ? client.getName() : "Client not found",
                balance.toPlainString());
    }
}
