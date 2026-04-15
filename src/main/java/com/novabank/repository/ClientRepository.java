package com.novabank.repository;

import com.novabank.model.Client;

import java.util.*;

public class ClientRepository {
    private final Map<Long, Client> clients = new HashMap<>();

    private long nextId = 1001L;

    public Client save(Client client) {
        if (client == null) throw new IllegalArgumentException("Client cannot be null.");
        if (client.getId() == null) {
            client.setId(nextId++);
        }
        clients.put(client.getId(), client);
        return client;
    }

    public Optional<Client> searchById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null.");
        return Optional.ofNullable(clients.get(id));
    }

    public Optional<Client> searchByDni(String dni) {
        if (dni == null || dni.isBlank()) throw new IllegalArgumentException("DNI cannot be null or blank");
        return clients.values().stream()
                .filter(c -> c.getDni().equals(dni))
                .findFirst();
    }

    public List<Client> searchAll() {
        return new ArrayList<>(clients.values());
    }

    public boolean existsByDni(String dni) {
        if (dni == null || dni.isBlank()) throw new IllegalArgumentException("DNI cannot be null or blank");
        return clients.values().stream().anyMatch(c -> c.getDni().equalsIgnoreCase(dni));
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null or blank");
        return clients.values().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByPhone(String phone) {
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("Phone cannot be null or blank");
        return clients.values().stream()
                .anyMatch(c -> c.getPhone().equals(phone));
    }
}
