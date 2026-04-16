package com.novabank;

import com.novabank.console.ClientMenu;
import com.novabank.repository.AccountRepository;
import com.novabank.repository.ClientRepository;
import com.novabank.repository.TransactionRepository;
import com.novabank.service.AccountService;
import com.novabank.service.ClientService;
import com.novabank.service.TransactionService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting NovaBank Digital Services...");

        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        ClientService clientService = new ClientService(clientRepository);
        AccountService accountService = new AccountService(accountRepository, clientService);
        TransactionService transactionService = new TransactionService(accountService, transactionRepository);

        Scanner scanner = new Scanner(System.in);
        ClientMenu clientMenu = new ClientMenu(clientService, scanner);

        boolean running = true;
        while(running) {
            showMainMenu();
            String option = scanner.nextLine().trim();

            switch(option) {
                case "1" -> clientMenu.show();
                case "5" -> {
                    System.out.println("See you soon.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Introduce a number between 1 and 5.");
            }
        }
    }

    private static void showMainMenu() {
        System.out.println();
        System.out.println("====================================");
        System.out.println(" NOVABANK - SYSTEM OPERATIONS");
        System.out.println("====================================");
        System.out.println("1. Client management");
        System.out.println("2. Account management");
        System.out.println("3. Financial operations");
        System.out.println("4. Inquiries");
        System.out.println("5. Exit");
        System.out.print("Select an option: ");
    }
};