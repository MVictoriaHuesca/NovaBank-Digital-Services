package com.novabank.model;

public class ClientBuilder {

    private String name;
    private String surname;
    private String dni;
    private String email;
    private String phone;

    public ClientBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ClientBuilder surname(String surname) {
        this.surname = surname;
        return this;
    }

    public ClientBuilder dni(String dni) {
        this.dni = dni;
        return this;
    }

    public ClientBuilder email(String email) {
        this.email = email;
        return this;
    }

    public ClientBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public Client build() {
        return new Client(name, surname, dni, email, phone);
    }
}
