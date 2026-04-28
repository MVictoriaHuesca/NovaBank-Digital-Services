package com.novabank.service;

import com.novabank.model.Client;
import com.novabank.model.ClientBuilder;
import com.novabank.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client buildClient(long id, String dni, String email, String phone) {
        Client c = new ClientBuilder()
                .name("Juan").surname("Pérez")
                .dni(dni).email(email).phone(phone)
                .build();
        c.setId(id);
        return c;
    }

    private void stubSave(long id) {
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(id);
            return c;
        });
    }

    @Test
    @DisplayName("Creating a client with valid input data should return client with assigned ID")
    void createClient_withValidData_shouldReturnClientWithId() {
        stubSave(1L);

        Client result = clientService.save("Juan", "Pérez", "12345678A", "juan@email.com", "600123456");

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Juan", result.getName());
        assertEquals("Pérez", result.getSurname());
        assertEquals("12345678A", result.getDni());
        assertEquals("juan@email.com", result.getEmail());
        assertEquals("600123456", result.getPhone());
    }

    @Test
    @DisplayName("Creating two different clients must assign two different IDs")
    void createClient_twoClients_mustHaveDifferentIds() {
        long[] idSeq = {1L};
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(idSeq[0]++);
            return c;
        });

        Client c1 = clientService.save("Juan", "Pérez",  "11111111A", "juan@email.com", "600000001");
        Client c2 = clientService.save("Ana",  "García", "22222222B", "ana@email.com",  "600000002");

        assertNotEquals(c1.getId(), c2.getId());
    }

    @Test
    @DisplayName("Listing clients should return all clients created")
    void listingClients_withTwoClients_shouldReturnList() {
        Client c1 = buildClient(1L, "11111111A", "juan@email.com", "600000001");
        Client c2 = buildClient(2L, "22222222B", "ana@email.com",  "600000002");
        when(clientRepository.searchAll()).thenReturn(List.of(c1, c2));

        List<Client> list = clientService.listClients();

        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Searching by DNI should return the correct client")
    void searchByDni_withExistingDni_shouldReturnClient() {
        Client client = buildClient(1L, "12345678A", "juan@email.com", "600123456");
        when(clientRepository.searchByDni("12345678A")).thenReturn(Optional.of(client));

        Client found = clientService.searchByDni("12345678A");

        assertNotNull(found);
        assertEquals("12345678A", found.getDni());
    }

    @Test
    @DisplayName("Search by ID should return the correct client")
    void searchById_withExistingId_shouldReturnClient() {
        Client client = buildClient(1L, "12345678A", "juan@email.com", "600123456");
        when(clientRepository.searchById(1L)).thenReturn(Optional.of(client));

        Client found = clientService.searchById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
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
        when(clientRepository.existsByDni("12345678A")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Pedro", "López", "12345678A", "pedro@email.com", "600000002"));

        assertTrue(ex.getMessage().contains("DNI"));
    }

    @Test
    @DisplayName("Creating a client with a duplicate email must throw an exception")
    void createClient_withDuplicatedEmail_shouldThrowException() {
        when(clientRepository.existsByEmail("juan@email.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                clientService.save("Pedro", "López", "99999999Z", "juan@email.com", "600000002"));
    }

    @Test
    @DisplayName("Creating a client with a duplicate phone number should throw an exception")
    void createClient_withDuplicatedPhone_shouldThrowException() {
        when(clientRepository.existsByPhone("600000001")).thenReturn(true);

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
        when(clientRepository.searchByDni("99999999Z")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchByDni("99999999Z"));
    }

    @Test
    @DisplayName("Searching for a non-existent ID should throw an exception")
    void searchById_withNonExistingId_shouldThrowException() {
        when(clientRepository.searchById(9999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchById(9999L));
    }

    @Test
    @DisplayName("Searching by null ID should throw an exception")
    void searchById_withNullId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchById(null));
    }

    @Test
    @DisplayName("Searching by null DNI should throw an exception")
    void searchByDni_withNullDni_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchByDni(null));
    }

    @Test
    @DisplayName("Searching by blank DNI should throw an exception")
    void searchByDni_withBlankDni_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                clientService.searchByDni("   "));
    }

    @Test
    @DisplayName("Searching by DNI in lowercase should find the client saved with uppercase")
    void searchByDni_withLowercaseDni_shouldReturnClient() {
        Client client = buildClient(1L, "12345678A", "juan@email.com", "600123456");
        when(clientRepository.searchByDni("12345678A")).thenReturn(Optional.of(client));

        Client found = clientService.searchByDni("12345678a");

        assertNotNull(found);
        assertEquals("12345678A", found.getDni());
    }

    @Test
    @DisplayName("Listing clients with no clients should return an empty list")
    void listClients_withNoClients_shouldReturnEmptyList() {
        when(clientRepository.searchAll()).thenReturn(List.of());

        List<Client> list = clientService.listClients();

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("Saving a client with whitespace around the name should trim and store it correctly")
    void createClient_withWhitespaceName_shouldStoreTrimmedName() {
        stubSave(1L);

        Client result = clientService.save("  Juan  ", "  Pérez  ", "12345678A", "juan@email.com", "600123456");

        assertEquals("Juan", result.getName());
        assertEquals("Pérez", result.getSurname());
    }
}
