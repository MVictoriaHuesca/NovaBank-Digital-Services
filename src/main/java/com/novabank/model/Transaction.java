package com.novabank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private Account account;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public Transaction(Account account, TransactionType type, BigDecimal amount){
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Account getAccount() { return account; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public TransactionType getType() { return type; }

    public BigDecimal getAmount() { return amount; }

    public void setId(Long id) { this.id = id; }

    public void setAccount(Account account) { this.account = account; }

    public void setType(TransactionType type) { this.type = type; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Transaction{id=%d, type=%s, amount=%s, createdAt=%s}",
                id, type, amount.toPlainString(), createdAt);
    }
}
