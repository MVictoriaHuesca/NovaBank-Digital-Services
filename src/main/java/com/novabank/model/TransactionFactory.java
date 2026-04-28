package com.novabank.model;

import java.math.BigDecimal;

public class TransactionFactory {

    private TransactionFactory() {}

    public static Transaction createDeposit(Account account, BigDecimal amount) {
        return new Transaction(account, TransactionType.DEPOSIT, amount);
    }

    public static Transaction createWithdrawal(Account account, BigDecimal amount) {
        return new Transaction(account, TransactionType.WITHDRAWAL, amount);
    }

    public static Transaction createOutgoingTransfer(Account account, BigDecimal amount) {
        return new Transaction(account, TransactionType.OUTGOING_TRANSFER, amount);
    }

    public static Transaction createIncomingTransfer(Account account, BigDecimal amount) {
        return new Transaction(account, TransactionType.INCOMING_TRANSFER, amount);
    }
}
