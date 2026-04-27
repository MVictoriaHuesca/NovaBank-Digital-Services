package com.novabank.controller;

import com.novabank.service.AccountService;
import com.novabank.view.AccountView;

public class AccountController {

    private final AccountService accountService;
    private final AccountView view;

    public AccountController(AccountService accountService, AccountView view) {
        this.accountService = accountService;
        this.view = view;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            view.showMenu();
            switch (view.readOption()) {
                case "1" -> createAccount();
                case "2" -> listClientAccounts();
                case "3" -> seeAccountInformation();
                case "4" -> back = true;
                default  -> view.showError("Invalid option. Introduce a number between 1 and 4.");
            }
        }
    }

    private void createAccount() {
        try {
            Long clientId = Long.parseLong(view.readClientId());
            view.showCreateSuccess(accountService.createAccount(clientId));
        } catch (NumberFormatException e) {
            view.showError("ERROR: ID must be a number.");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void listClientAccounts() {
        try {
            Long clientId = Long.parseLong(view.readClientIdForList());
            view.showAccountList(accountService.listClientAccounts(clientId));
        } catch (NumberFormatException e) {
            view.showError("ERROR: ID must be a number.");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    private void seeAccountInformation() {
        try {
            view.showAccount(accountService.searchByAccountNumber(view.readAccountNumber()));
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }
}
