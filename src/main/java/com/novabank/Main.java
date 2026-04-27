package com.novabank;

import com.novabank.controller.AccountController;
import com.novabank.controller.ClientController;
import com.novabank.controller.InquiryController;
import com.novabank.controller.TransactionController;
import com.novabank.repository.AccountRepository;
import com.novabank.repository.ClientRepository;
import com.novabank.repository.TransactionRepository;
import com.novabank.service.AccountService;
import com.novabank.service.ClientService;
import com.novabank.service.TransactionService;
import com.novabank.view.AccountView;
import com.novabank.view.ClientView;
import com.novabank.view.InquiryView;
import com.novabank.view.TransactionView;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        
        System.out.println("Starting NovaBank Digital Services...");

        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        ClientService clientService = new ClientService(clientRepository);
        AccountService accountService = new AccountService(accountRepository, clientService);
        TransactionService transactionService = new TransactionService(accountService, transactionRepository);

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        ClientController clientController         = new ClientController(clientService, new ClientView(scanner));
        AccountController accountController       = new AccountController(accountService, new AccountView(scanner));
        TransactionController transactionController = new TransactionController(transactionService, new TransactionView(scanner));
        InquiryController inquiryController       = new InquiryController(transactionService, new InquiryView(scanner));

        boolean running = true;
        while(running) {
            showMainMenu();
            String option = scanner.nextLine().trim();

            switch(option) {
                case "1" -> clientController.show();
                case "2" -> accountController.show();
                case "3" -> transactionController.show();
                case "4" -> inquiryController.show();
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