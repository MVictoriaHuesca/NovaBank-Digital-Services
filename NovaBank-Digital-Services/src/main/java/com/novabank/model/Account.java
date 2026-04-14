package com.novabank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private Long id;
    private String numberAccount;
    private Client client;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private List<Transaction> transactionList;

    public Account(Client client) {
        this.client = client;
        this.numberAccount = numberAccount;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.transactionList = new ArrayList<>();
    }


    // getters

    public Long getId() {
        return id;
    }

    public String getNumberAccount() {
        return numberAccount;
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

    // setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setNumberAccount(String numberAccount) {
        this.numberAccount = numberAccount;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }


}
