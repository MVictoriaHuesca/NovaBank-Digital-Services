package com.novabank.repository.inmemory;

import com.novabank.model.Client;
import com.novabank.repository.ClientRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryClientRepository implements ClientRepository {

    private final Map<Long, Client> clients = new HashMap<>();
    private long nextId = 1001L;

    @Override
    public Client save(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null.");
        }
        if (client.getId() == null) {
            client.setId(nextId++);
        }
        clients.put(client.getId(), client);
        return client;
    }

    @Override
    public Optional<Client> searchById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        return Optional.ofNullable(clients.get(id));
    }

    @Override
    public Optional<Client> searchByDni(String dni) {
        return clients.values().stream()
                .filter(c -> c.getDni().equals(dni))
                .findFirst();
    }

    @Override
    public List<Client> searchAll() {
        return new ArrayList<>(clients.values());
    }

    @Override
    public boolean existsByDni(String dni) {
        return clients.values().stream()
                .anyMatch(c -> c.getDni().equals(dni));
    }

    @Override
    public boolean existsByEmail(String email) {
        return clients.values().stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return clients.values().stream()
                .anyMatch(c -> c.getPhone().equals(phone));
    }
}
