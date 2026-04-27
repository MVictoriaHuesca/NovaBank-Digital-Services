package com.novabank.view;

import com.novabank.model.Transaction;
import com.novabank.model.TransactionType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class InquiryView {

    private final Scanner scanner;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT      = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InquiryView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println();
        System.out.println("--- INQUIRIES ---");
        System.out.println("1. Check balance");
        System.out.println("2. Movements history");
        System.out.println("3. Movements by date range");
        System.out.println("4. Go back");
        System.out.print("Select an option: ");
    }

    public String readOption() {
        return scanner.nextLine().trim();
    }

    public String readAccountNumber() {
        System.out.println();
        System.out.print("Introduce account number: ");
        return scanner.nextLine().trim();
    }

    public String readAccountNumberForRange() {
        System.out.println();
        System.out.print("Account number: ");
        return scanner.nextLine().trim();
    }

    public String readStartDate() {
        System.out.print("Start date (yyyy-MM-dd): ");
        return scanner.nextLine().trim();
    }

    public String readEndDate() {
        System.out.print("End date   (yyyy-MM-dd): ");
        return scanner.nextLine().trim();
    }

    public void showBalance(BigDecimal balance) {
        System.out.printf("Actual balance: %,.2f €%n", balance);
    }

    public void showHistory(String accountNumber, List<Transaction> transactions) {
        System.out.println();
        System.out.println("Movements history - " + accountNumber + ":");
        showMovements(transactions);
    }

    public void showHistoryByDateRange(String from, String to, List<Transaction> transactions) {
        System.out.println();
        System.out.println("Movements from " + from + " to " + to + ":");
        showMovements(transactions);
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

    public void showError(String message) {
        System.out.println(message);
    }
}
