package com.novabank.controller;

import com.novabank.service.TransactionService;
import com.novabank.view.InquiryView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InquiryController {

    private final TransactionService transactionService;
    private final InquiryView view;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InquiryController(TransactionService transactionService, InquiryView view) {
        this.transactionService = transactionService;
        this.view = view;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            view.showMenu();
            switch (view.readOption()) {
                case "1" -> checkBalance();
                case "2" -> seeHistory();
                case "3" -> seeHistoryByDateRange();
                case "4" -> back = true;
                default  -> view.showError("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void checkBalance() {
        String number = view.readAccountNumber();
        try {
            view.showBalance(transactionService.getBalance(number));
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void seeHistory() {
        String number = view.readAccountNumber();
        try {
            view.showHistory(number, transactionService.getHistory(number));
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void seeHistoryByDateRange() {
        String number  = view.readAccountNumberForRange();
        String fromStr = view.readStartDate();
        String toStr   = view.readEndDate();

        try {
            LocalDate from = LocalDate.parse(fromStr, DATE_FORMAT);
            LocalDate to   = LocalDate.parse(toStr,   DATE_FORMAT);

            if (to.isBefore(from)) {
                view.showError("ERROR: End date cannot be before start date.");
                return;
            }

            view.showHistoryByDateRange(fromStr, toStr,
                    transactionService.getHistoryByDate(number, from, to));
        } catch (DateTimeParseException e) {
            view.showError("ERROR: Invalid date format. Use yyyy-MM-dd (e.g. 2024-03-15).");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }
}
