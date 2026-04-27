package com.novabank.controller;

import com.novabank.service.ClientService;
import com.novabank.view.ClientView;

public class ClientController {

    private final ClientService clientService;
    private final ClientView view;

    public ClientController(ClientService clientService, ClientView view) {
        this.clientService = clientService;
        this.view = view;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            view.showMenu();
            switch (view.readOption()) {
                case "1" -> createClient();
                case "2" -> searchClient();
                case "3" -> listClients();
                case "4" -> back = true;
                default  -> view.showError("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void createClient() {
        System.out.println();
        String name    = view.readName();
        String surname = view.readSurname();
        String dni     = view.readDni();
        String email   = view.readEmail();
        String phone   = view.readPhone();
        try {
            view.showCreateSuccess(clientService.save(name, surname, dni, email, phone));
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void searchClient() {
        view.showSearchMenu();
        switch (view.readOption()) {
            case "1" -> {
                try {
                    view.showClient(clientService.searchByDni(view.readSearchDni()));
                } catch (IllegalArgumentException e) {
                    view.showError(e.getMessage());
                }
            }
            case "2" -> {
                try {
                    Long id = Long.parseLong(view.readSearchId());
                    view.showClient(clientService.searchById(id));
                } catch (NumberFormatException e) {
                    view.showError("ERROR: ID must be a number.");
                } catch (IllegalArgumentException e) {
                    view.showError(e.getMessage());
                }
            }
            case "3" -> { /* go back */ }
            default -> view.showError("Invalid option. Introduce a number between 1 and 3.");
        }
    }

    private void listClients() {
        view.showClientList(clientService.listClients());
    }
}
