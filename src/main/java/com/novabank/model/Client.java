package com.novabank.model;

import java.time.LocalDateTime;

public class Client {

    private Long id;
    private String name;
    private String surname;
    private String dni;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    // Public constructor
    public Client(String name, String surname, String dni, String email, String phone){
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    // Empty constructor
    public Client(){};

    // Getters
    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDni() {
        return dni;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString(){
        return String.format("Client{id=%d, name='%s %s', dni='%s', email='%s', phone='%s'}",
                id, name, surname, dni, email, phone);
    }
}
