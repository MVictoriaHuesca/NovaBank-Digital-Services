package com.novabank.console;

import com.novabank.model.Account;
import com.novabank.service.AccountService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AccountMenu {
    private final AccountService accountService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AccountMenu(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- ACCOUNT MANAGEMENT ---");
            System.out.println("1. Create account");
            System.out.println("2. List client accounts");
            System.out.println("3. See account information");
            System.out.println("4. Go back");
            System.out.print("Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> createAccount();
                case "2" -> listClientAccounts();
                case "3" -> seeAccountInformation();
                case "4" -> back = true;
                default  -> System.out.println("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void createAccount() {
        System.out.println();
        System.out.print("Enter the ID of the account-holding customer: ");
        try {
            Long clientId = Long.parseLong(scanner.nextLine().trim());
            Account account = accountService.createAccount(clientId);
            System.out.println();
            System.out.println("Account created successfully.");
            System.out.println("Account number: " + account.getAccountNumber());
            System.out.println("Account holder: " + account.getClient().getName() + " " + account.getClient().getSurname()
                    + " (ID: " + account.getClient().getId() + ")");
            System.out.printf("Starting balance:    %,.2f €%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listClientAccounts() {
        System.out.println();
        System.out.print("Introduce client ID: ");
        try {
            Long clientId = Long.parseLong(scanner.nextLine().trim());
            List<Account> accounts = accountService.listClientAccounts(clientId);

            if (accounts.isEmpty()) {
                System.out.println("Client has no accounts.");
                return;
            }

            System.out.println();
            System.out.println(
                    "Accounts of " + accounts.get(0).getClient().getName() + " " + accounts.get(0).getClient().getSurname() + ":");

            int maxAccLen = 26;
            for (Account a : accounts) {
                maxAccLen = Math.max(maxAccLen, a.getAccountNumber().length());
            }

            String formatRow = "%-" + maxAccLen + "s | %s%n";

            System.out.printf(formatRow, "Account number", "Balance");
            System.out.println("-".repeat(maxAccLen + 15));
            for (Account a : accounts) {
                System.out.printf("%-" + maxAccLen + "s | %,.2f €%n",
                        a.getAccountNumber(), a.getBalance());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void seeAccountInformation() {
        System.out.println();
        System.out.print("Introduce account number: ");
        String number = scanner.nextLine().trim();
        try {
            Account a = accountService.searchByAccountNumber(number);
            System.out.println();
            System.out.println("Account number: " + a.getAccountNumber());
            System.out.println("Account holder: " + a.getClient().getName() + " " + a.getClient().getSurname());
            System.out.printf ("Balance:            %,.2f €%n", a.getBalance());
            System.out.println("Creation date: " + a.getCreatedAt().format(DATE_FORMAT));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
