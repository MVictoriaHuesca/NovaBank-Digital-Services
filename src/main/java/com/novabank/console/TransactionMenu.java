package com.novabank.console;

import com.novabank.model.Account;
import com.novabank.service.TransactionService;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransactionMenu {
    private final TransactionService transactionService;
    private final Scanner scanner;

    public TransactionMenu(TransactionService transactionService, Scanner scanner) {
        this.transactionService = transactionService;
        this.scanner = scanner;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- FINANCTIAL OPERATIONS ---");
            System.out.println("1. Deposit money");
            System.out.println("2. Withdrawal");
            System.out.println("3. Transfer between accounts");
            System.out.println("4. Go back");
            System.out.print("Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> deposit();
                case "2" -> withdrawal();
                case "3" -> transfer();
                case "4" -> back = true;
                default  -> System.out.println("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void deposit() {
        System.out.println();
        System.out.print("Número de cuenta: ");
        String number = scanner.nextLine().trim();
        System.out.print("Amount to deposit (€): ");
        try {
            BigDecimal amount = readAmount();
            Account account = transactionService.deposit(number, amount);
            System.out.println();
            System.out.println("Deposit made successfully.");
            System.out.println("Account:      " + account.getNumberAccount());
            System.out.printf ("Amount:     +%,.2f €%n", amount);
            System.out.printf ("New balance: %,.2f €%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void withdrawal() {
        System.out.println();
        System.out.print("Account number: ");
        String number = scanner.nextLine().trim();
        System.out.print("Withdrawal amount (€): ");
        try {
            BigDecimal amount = readAmount();
            Account account = transactionService.withdrawal(number, amount);
            System.out.println();
            System.out.println("Withdrawal successfully made.");
            System.out.println("Account:      " + account.getNumberAccount());
            System.out.printf ("Amount:     -%,.2f €%n", amount);
            System.out.printf ("New balance: %,.2f €%n", account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void transfer() {
        System.out.println();
        System.out.print("Origin account number: ");
        String from  = scanner.nextLine().trim();
        System.out.print("Destinatary account number: ");
        String to = scanner.nextLine().trim();
        System.out.print("Amount to transfer (€): ");
        try {
            BigDecimal amount = readAmount();
            transactionService.transfer(from, to, amount);
            System.out.println();
            System.out.println("Transfer successfully completed.");
            System.out.printf ("From account:  %s → -%,.2f €%n", from,  amount);
            System.out.printf ("To account: %s → +%,.2f €%n", to, amount);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private BigDecimal readAmount() {
        String input = scanner.nextLine().trim().replace(",", ".");
        return new BigDecimal(input);
    }
}
