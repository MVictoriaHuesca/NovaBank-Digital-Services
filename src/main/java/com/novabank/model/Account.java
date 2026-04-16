package com.novabank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        if (amount.compareTo(this.balance) > 0){
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return String.format("Account{accountNumber='%s', client='%s', balance=%s}",
                accountNumber,
                client != null ? client.getName() : "Client not found",
                balance.toPlainString());
    }
}
