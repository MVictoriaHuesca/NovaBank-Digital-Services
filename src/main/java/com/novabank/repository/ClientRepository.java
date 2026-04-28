package com.novabank.repository;

import com.novabank.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> searchById(Long id);
    Optional<Client> searchByDni(String dni);
    List<Client> searchAll();
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
