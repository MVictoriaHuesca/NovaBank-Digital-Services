package com.novabank.controller;

import com.novabank.service.TransactionService;
import com.novabank.view.TransactionView;

import java.math.BigDecimal;

public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionView view;

    public TransactionController(TransactionService transactionService, TransactionView view) {
        this.transactionService = transactionService;
        this.view = view;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            view.showMenu();
            switch (view.readOption()) {
                case "1" -> deposit();
                case "2" -> withdrawal();
                case "3" -> transfer();
                case "4" -> back = true;
                default  -> view.showError("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void deposit() {
        String number = view.readAccountNumber();
        try {
            BigDecimal amount = view.readAmount("Amount to deposit (€): ");
            view.showDepositSuccess(transactionService.deposit(number, amount), amount);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void withdrawal() {
        String number = view.readAccountNumber();
        try {
            BigDecimal amount = view.readAmount("Withdrawal amount (€): ");
            view.showWithdrawalSuccess(transactionService.withdrawal(number, amount), amount);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void transfer() {
        String from = view.readOriginAccountNumber();
        String to   = view.readDestinationAccountNumber();
        try {
            BigDecimal amount = view.readAmount("Amount to transfer (€): ");
            transactionService.transfer(from, to, amount);
            view.showTransferSuccess(from, to, amount);
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }
}
