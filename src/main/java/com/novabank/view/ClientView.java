package com.novabank.view;

import com.novabank.model.Client;

import java.util.List;
import java.util.Scanner;

public class ClientView {

    private final Scanner scanner;

    public ClientView(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println();
        System.out.println("--- CLIENT MANAGEMENT ---");
        System.out.println("1. Create new client");
        System.out.println("2. Search client");
        System.out.println("3. List clients");
        System.out.println("4. Back");
        System.out.print("Select an option: ");
    }

    public void showSearchMenu() {
        System.out.println();
        System.out.println("Search by:");
        System.out.println("1. DNI");
        System.out.println("2. ID");
        System.out.println("3. Go back");
        System.out.print("Select an option: ");
    }

    public String readOption() {
        return scanner.nextLine().trim();
    }

    public String readName() {
        System.out.print("Name: ");
        return scanner.nextLine();
    }

    public String readSurname() {
        System.out.print("Surname: ");
        return scanner.nextLine();
    }

    public String readDni() {
        System.out.print("DNI/NIF: ");
        return scanner.nextLine();
    }

    public String readEmail() {
        System.out.print("Email: ");
        return scanner.nextLine();
    }

    public String readPhone() {
        System.out.print("Phone: ");
        return scanner.nextLine();
    }

    public String readSearchDni() {
        System.out.print("Introduce DNI/NIF: ");
        return scanner.nextLine();
    }

    public String readSearchId() {
        System.out.print("Introduce ID: ");
        return scanner.nextLine().trim();
    }

    public void showCreateSuccess(Client client) {
        System.out.println();
        System.out.println("Client created successfully.");
        System.out.println("ID client: " + client.getId());
    }

    public void showClient(Client client) {
        System.out.println();
        System.out.println("Client found:");
        System.out.println("ID:       " + client.getId());
        System.out.println("Name:     " + client.getName() + " " + client.getSurname());
        System.out.println("DNI:      " + client.getDni());
        System.out.println("Email:    " + client.getEmail());
        System.out.println("Phone:    " + client.getPhone());
    }

    public void showClientList(List<Client> clients) {
        System.out.println();

        if (clients.isEmpty()) {
            System.out.println("There are no clients in the system.");
            return;
        }

        int maxId    = 6;
        int maxName  = 20;
        int maxDni   = 12;
        int maxEmail = 25;
        int maxPhone = 12;

        for (Client c : clients) {
            maxId    = Math.max(maxId,    String.valueOf(c.getId()).length());
            maxName  = Math.max(maxName,  (c.getName() + " " + c.getSurname()).length());
            maxDni   = Math.max(maxDni,   c.getDni().length());
            maxEmail = Math.max(maxEmail, c.getEmail().length());
            maxPhone = Math.max(maxPhone, c.getPhone().length());
        }

        String header = "%-" + maxId + "s | %-" + maxName + "s | %-" + maxDni + "s | %-" + maxEmail + "s | %-" + maxPhone + "s%n";
        String row    = "%-" + maxId + "d | %-" + maxName + "s | %-" + maxDni + "s | %-" + maxEmail + "s | %-" + maxPhone + "s%n";

        System.out.printf(header, "ID", "Name", "DNI", "Email", "Phone");
        System.out.println("-".repeat(maxId + maxName + maxDni + maxEmail + maxPhone + 12));

        for (Client c : clients) {
            System.out.printf(row, c.getId(), c.getName() + " " + c.getSurname(),
                    c.getDni(), c.getEmail(), c.getPhone());
        }
    }

    public void showError(String message) {
        System.out.println(message);
    }
}
