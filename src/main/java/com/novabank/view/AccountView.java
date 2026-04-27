package com.novabank.view;

import com.novabank.model.Account;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AccountView {

    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AccountView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println();
        System.out.println("--- ACCOUNT MANAGEMENT ---");
        System.out.println("1. Create account");
        System.out.println("2. List client accounts");
        System.out.println("3. See account information");
        System.out.println("4. Go back");
        System.out.print("Select an option: ");
    }

    public String readOption() {
        return scanner.nextLine().trim();
    }

    public String readClientId() {
        System.out.println();
        System.out.print("Enter the ID of the account-holding customer: ");
        return scanner.nextLine().trim();
    }

    public String readClientIdForList() {
        System.out.println();
        System.out.print("Introduce client ID: ");
        return scanner.nextLine().trim();
    }

    public String readAccountNumber() {
        System.out.println();
        System.out.print("Introduce account number: ");
        return scanner.nextLine().trim();
    }

    public void showCreateSuccess(Account account) {
        System.out.println();
        System.out.println("Account created successfully.");
        System.out.println("Account number: " + account.getAccountNumber());
        System.out.println("Account holder: " + account.getClient().getName() + " " + account.getClient().getSurname()
                + " (ID: " + account.getClient().getId() + ")");
        System.out.printf("Starting balance:    %,.2f €%n", account.getBalance());
    }

    public void showAccountList(List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("Client has no accounts.");
            return;
        }

        System.out.println();
        System.out.println("Accounts of " + accounts.get(0).getClient().getName()
                + " " + accounts.get(0).getClient().getSurname() + ":");

        int maxAccLen = 26;
        for (Account a : accounts) {
            maxAccLen = Math.max(maxAccLen, a.getAccountNumber().length());
        }

        System.out.printf("%-" + maxAccLen + "s | %s%n", "Account number", "Balance");
        System.out.println("-".repeat(maxAccLen + 15));
        for (Account a : accounts) {
            System.out.printf("%-" + maxAccLen + "s | %,.2f €%n", a.getAccountNumber(), a.getBalance());
        }
    }

    public void showAccount(Account a) {
        System.out.println();
        System.out.println("Account number: " + a.getAccountNumber());
        System.out.println("Account holder: " + a.getClient().getName() + " " + a.getClient().getSurname());
        System.out.printf ("Balance:            %,.2f €%n", a.getBalance());
        System.out.println("Creation date: " + a.getCreatedAt().format(DATE_FORMAT));
    }

    public void showError(String message) {
        System.out.println(message);
    }
}
