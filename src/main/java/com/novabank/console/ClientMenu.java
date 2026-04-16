package com.novabank.console;

import com.novabank.model.Client;
import com.novabank.service.ClientService;

import java.util.List;
import java.util.Scanner;

public class ClientMenu {
    private final ClientService clientService;
    private final Scanner scanner;

    public ClientMenu(ClientService clientService, Scanner scanner) {
        this.clientService = clientService;
        this.scanner = scanner;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Create new client");
            System.out.println("2. Search client");
            System.out.println("3. List clients");
            System.out.println("4. Back");
            System.out.print("Select option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> createClient();
                case "2" -> searchClient();
                case "3" -> listClients();
                case "4" -> back = true;
                default  -> System.out.println("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void createClient() {
        System.out.println();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Surname: ");
        String surname = scanner.nextLine();
        System.out.print("DNI/NIF: ");
        String dni = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone  = scanner.nextLine();

        try {
            Client client = clientService.save(name, surname, dni, email, phone);
            System.out.println();
            System.out.println("Client created successfully.");
            System.out.println("ID client: " + client.getId());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchClient() {
        System.out.println();
        System.out.println("Search by:");
        System.out.println("1. DNI");
        System.out.println("2. ID");
        System.out.println("3. Go back");
        System.out.print("Select an option: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Introduce DNI/NIF: ");
                String dni = scanner.nextLine();
                try {
                    showClient(clientService.searchByDni(dni));
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            case "2" -> {
                System.out.print("Introduce ID: ");
                try {
                    Long id = Long.parseLong(scanner.nextLine().trim());
                    showClient(clientService.searchById(id));
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            case "3" -> show();
            default -> System.out.println("Invalid option. Introduce a number between 1 and 4.");
        }
    }

    private void showClient(Client client) {
        System.out.println();
        System.out.println("Client found:");
        System.out.println("ID:       " + client.getId());
        System.out.println("Name:     " + client.getName() + " " + client.getSurname());
        System.out.println("DNI:      " + client.getDni());
        System.out.println("Email:    " + client.getEmail());
        System.out.println("Phone:    " + client.getPhone());
    }

    private void listClients() {
        List<Client> clients = clientService.listClients();
        System.out.println();

        if (clients.isEmpty()) {
            System.out.println("There are no clients in the system.");
            return;
        }

        System.out.printf("%-6s | %-20s | %-12s | %-25s | %-12s%n",
                "ID", "Name", "DNI", "Email", "Teléfono");
        System.out.println("-".repeat(85));


        for (Client c : clients) {
            String fullName = c.getName() + " " + c.getSurname();
                    System.out.printf("%-6d | %-20s | %-12s | %-25s | %-12s%n",
                    c.getId(),
                    fullName,
                    c.getDni(),
                    c.getEmail(),
                    c.getPhone());
        }
    }
}
