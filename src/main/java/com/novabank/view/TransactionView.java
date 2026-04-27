package com.novabank.view;

import com.novabank.model.Account;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransactionView {

    private final Scanner scanner;

    public TransactionView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println();
        System.out.println("--- FINANCIAL OPERATIONS ---");
        System.out.println("1. Deposit money");
        System.out.println("2. Withdrawal");
        System.out.println("3. Transfer between accounts");
        System.out.println("4. Go back");
        System.out.print("Select an option: ");
    }

    public String readOption() {
        return scanner.nextLine().trim();
    }

    public String readAccountNumber() {
        System.out.println();
        System.out.print("Account number: ");
        return scanner.nextLine().trim();
    }

    public String readOriginAccountNumber() {
        System.out.println();
        System.out.print("Origin account number: ");
        return scanner.nextLine().trim();
    }

    public String readDestinationAccountNumber() {
        System.out.print("Destination account number: ");
        return scanner.nextLine().trim();
    }

    public BigDecimal readAmount(String label) {
        System.out.print(label);
        try {
            return new BigDecimal(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERROR: Invalid amount. Please enter a numeric value.");
        }
    }

    public void showDepositSuccess(Account account, BigDecimal amount) {
        System.out.println();
        System.out.println("Deposit made successfully.");
        System.out.println("Account:      " + account.getAccountNumber());
        System.out.printf ("Amount:     +%,.2f €%n", amount);
        System.out.printf ("New balance: %,.2f €%n", account.getBalance());
    }

    public void showWithdrawalSuccess(Account account, BigDecimal amount) {
        System.out.println();
        System.out.println("Withdrawal successfully made.");
        System.out.println("Account:      " + account.getAccountNumber());
        System.out.printf ("Amount:     -%,.2f €%n", amount);
        System.out.printf ("New balance: %,.2f €%n", account.getBalance());
    }

    public void showTransferSuccess(String from, String to, BigDecimal amount) {
        System.out.println();
        System.out.println("Transfer successfully completed.");
        System.out.printf ("From account:  %s → -%,.2f €%n", from, amount);
        System.out.printf ("To account: %s → +%,.2f €%n", to, amount);
    }

    public void showError(String message) {
        System.out.println(message);
    }
}
