package com.novabank.console;

import com.novabank.model.Transaction;
import com.novabank.model.TransactionType;
import com.novabank.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class InquiryMenu {
    private final TransactionService transactionService;
    private final Scanner scanner;

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InquiryMenu(TransactionService transactionService, Scanner scanner) {
        this.transactionService = transactionService;
        this.scanner = scanner;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- INQUIRIES ---");
            System.out.println("1. Check balance");
            System.out.println("2. Movements history");
            System.out.println("3. Movements by date range");
            System.out.println("4. Go back");
            System.out.print("Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> checkBalance();
                case "2" -> seeHistory();
                case "3" -> seeHistoryByDateRange();
                case "4" -> back = true;
                default  -> System.out.println("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void checkBalance() {
        System.out.println();
        System.out.print("Introduce account number: ");
        String number = scanner.nextLine().trim();
        try {
            BigDecimal balance = transactionService.getBalance(number);
            System.out.printf("Actual balance: %,.2f €%n", balance);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void seeHistory() {
        System.out.println();
        System.out.print("Introduce account number: ");
        String number = scanner.nextLine().trim();
        try {
            List<Transaction> transactions = transactionService.getHistory(number);
            System.out.println();
            System.out.println("Movements history - " + number + ":");
            showMovements(transactions);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void seeHistoryByDateRange() {
        System.out.println();
        System.out.print("Account number: ");
        String number = scanner.nextLine().trim();
        System.out.print("Start date (yyyy-MM-dd): ");
        String fromStr = scanner.nextLine().trim();
        System.out.print("End date   (yyyy-MM-dd): ");
        String toStr = scanner.nextLine().trim();

        try {
            LocalDate from = LocalDate.parse(fromStr, DATE_FORMAT);
            LocalDate to = LocalDate.parse(toStr, DATE_FORMAT);

            if (to.isBefore(from)) {
                System.out.println("ERROR: End date cannot be before start date.");
                return;
            }

            List<Transaction> transactions =
                    transactionService.getHistoryByDate(number, from, to);
            System.out.println();
            System.out.println("Movements from " + fromStr + " to " + toStr + ":");
            showMovements(transactions);
        } catch (DateTimeParseException e) {
            System.out.println("ERROR: Invalid date format. Use yyyy-MM-dd (e.g. 2024-03-15).");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showMovements(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No movements registered.");
            return;
        }

        System.out.printf("%-21s | %-26s | %s%n", "Date", "Type", "Amount");
        System.out.println("-".repeat(65));

        for (Transaction t : transactions) {
            String sign = (t.getType() == TransactionType.OUTGOING_TRANSFER ||
                    t.getType() == TransactionType.WITHDRAWAL) ? "-" : "+";
            System.out.printf("%-21s | %-26s | %s%,.2f €%n",
                    t.getCreatedAt().format(DATE_TIME_FORMAT),
                    t.getType(),
                    sign,
                    t.getAmount());
        }
    }
}
