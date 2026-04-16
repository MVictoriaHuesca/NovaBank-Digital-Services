package com.novabank.service;

import com.novabank.model.Client;
import com.novabank.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {
    private ClientRepository clientRepository;
    private ClientService clientService;

    // Se ejecuta antes de cada test: crea instancias limpias
    @BeforeEach
    void setUp() {
        clientRepository = new ClientRepository();
        clientService = new ClientService(clientRepository);
    }

    @Test
    @DisplayName("Creating a client with valid input data should return client with assigned ID")
    void createClient_withValidData_shouldReturnClientWithId() {
        // Arrange
        String name = "Juan";
        String surname = "Pérez";
        String dni = "12345678A";
        String email = "juan@email.com";
        String phone = "600123456";

        // Act
        Client result = clientService.save(name, surname, dni, email, phone);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Juan", result.getName());
        assertEquals("Pérez", result.getSurname());
        assertEquals("12345678A", result.getDni());
        assertEquals("juan@email.com", result.getEmail());
        assertEquals("600123456", result.getPhone());
    }

    @Test
    @DisplayName("Creating two different clients must assign two differents IDs")
    void createClient_twoClients_mustHaveDifferentIds() {
        // Act
        Client c1 = clientService.save("Juan", "Pérez",  "11111111A", "juan@email.com", "600000001");
        Client c2 = clientService.save("Ana",  "García", "22222222B", "ana@email.com",  "600000002");

        // Assert
        assertNotEquals(c1.getId(), c2.getId());
    }

    @Test
    @DisplayName("Listing clients should return all clients created")
    void listingClients_withTwoClients_shouldReturnList() {
        // Arrange
        clientService.save("Juan", "Pérez",  "11111111A", "juan@email.com", "600000001");
        clientService.save("Ana",  "García", "22222222B", "ana@email.com",  "600000002");

        // Act
        List<Client> list = clientService.listClients();

        // Assert
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Searching by ID number should return the correct client")
    void searchByDni_withExistingDni_shouldReturnClient() {
        // Arrange
        clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");

        // Act
        Client found = clientService.searchByDni("12345678A");

        // Assert
        assertNotNull(found);
        assertEquals("12345678A", found.getDni());
    }

    @Test
    @DisplayName("Search by ID should return the correct client.")
    void searchById_withExistingId_shouldReturnClient() {
        // Arrange
        Client created = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");

        // Act
        Client found = clientService.searchById(created.getId());

        // Assert
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @DisplayName("Creating a client with an empty name should throw an exception")
    void createClient_withEmptyName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("", "Pérez", "12345678A", "juan@email.com", "600123456"));
    }

    @Test
    @DisplayName("Creating a client with null last names should throw an exception")
    void createClient_withNullSurname_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Juan", null, "12345678A", "juan@email.com", "600123456"));
    }

    @Test
    @DisplayName("Creating client with empty DNI should throw exception")
    void createClient_withEmptyDni_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Juan", "Pérez", "  ", "juan@email.com", "600123456"));
    }

    @Test
    @DisplayName("Creating a client with a duplicate DNI must throw an exception")
    void createClient_withDuplicatedDni_shouldThrowException() {
        // Arrange
        clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600000001");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Pedro", "López", "12345678A", "pedro@email.com", "600000002"));

        assertTrue(ex.getMessage().contains("DNI"));
    }

    @Test
    @DisplayName("Creating a client with a duplicate email must throw an exception")
    void createClient_withDuplicatedEmail_shouldThrowException() {
        // Arrange
        clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600000001");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Pedro", "López", "99999999Z", "juan@email.com", "600000002"));
    }

    @Test
    @DisplayName("Creating a client with a duplicate phone number should throw an exception")
    void createCliente_withDuplicatedPhone_shouldThrowException() {
        // Arrange
        clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600000001");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Pedro", "López", "99999999Z", "pedro@email.com", "600000001"));
    }

    @Test
    @DisplayName("Creating a client with an email without @ should throw an exception")
    void createClient_withEmailWithoutAtSymbol_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Juan", "Pérez", "12345678A", "juanemail.com", "600123456"));
    }

    @Test
    @DisplayName("Creating a client with an email without a dot after the @ should throw an exception")
    void createClient_withEmailWithoutDot_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Juan", "Pérez", "12345678A", "juan@emailcom", "600123456"));
    }

    @Test
    @DisplayName("Searching for a non-existent DNI should throw an exception")
    void searchByDni_withNonExistingDni_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchByDni("99999999Z"));
    }

    @Test
    @DisplayName("Searching for a non-existent ID should throw an exception")
    void searchById_withNonExistingId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchById(9999L));
    }
}
