package com.novabank.service;

import com.novabank.model.Client;
import com.novabank.repository.ClientRepository;

import java.util.List;

public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client save(String name, String surname, String dni, String email, String phone) {
        String trimmedName = name != null ? name.trim() : null;
        String trimmedSurname = surname != null ? surname.trim() : null;
        String trimmedDni = dni != null ? dni.trim().toUpperCase() : null;
        String trimmedEmail = email != null ? email.trim() : null;
        String trimmedPhone = phone != null ? phone.trim() : null;

        validateRequiredFields(trimmedName, trimmedSurname, trimmedDni, trimmedEmail, trimmedPhone);
        validateEmailFormat(trimmedEmail);
        validateUniqueDni(trimmedDni);
        validateUniqueEmail(trimmedEmail);
        validateUniquePhone(trimmedPhone);

        return clientRepository.save(new Client(trimmedName, trimmedSurname, trimmedDni, trimmedEmail, trimmedPhone));
    }

    public Client searchById(Long id) {
        if (id == null) throw new IllegalArgumentException("ERROR: ID cannot be null.");
        return clientRepository.searchById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Could not find a client with ID " + id + "."));
    }

    public Client searchByDni(String dni) {
        if (dni == null || dni.isBlank()) throw new IllegalArgumentException("ERROR: DNI cannot be null or blank.");
        return clientRepository.searchByDni(dni.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Could not find a client with DNI " + dni + "."));
    }

    public List<Client> listClients() {
        return clientRepository.searchAll();
    }

    private void validateRequiredFields(String name, String surname, String dni, String email, String phone) {
        if(name == null || name.isBlank()) throw new IllegalArgumentException("ERROR: Name is required.");
        if(surname == null || surname.isBlank()) throw new IllegalArgumentException("ERROR: Surname is required.");
        if(dni == null || dni.isBlank()) throw new IllegalArgumentException("ERROR: DNI is required.");
        if(email == null || email.isBlank()) throw new IllegalArgumentException("ERROR: Email is required.");
        if(phone == null || phone.isBlank()) throw new IllegalArgumentException("ERROR: Phone is required.");
    }

    private void validateEmailFormat(String email) {
        if(email == null || !email.contains("@") || !email.substring(email.indexOf("@")).contains(".")) {
            throw new IllegalArgumentException("ERROR: Invalid email format.");
        }
    }

    private void validateUniqueDni(String dni) {
        if(clientRepository.existsByDni(dni)) {
            throw new IllegalArgumentException("ERROR: A client with DNI " + dni + " already exists.");
        }
    }

    private void validateUniqueEmail(String email) {
        if(clientRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("ERROR: A client with email " + email + " already exists.");
        }
    }

    private void validateUniquePhone(String phone) {
        if(clientRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("ERROR: A client with phone number " + phone + " already exists.");
        }
    }
}
