package com.safetynet.floodStation;

import com.safetynet.controller.FireAddressController;
import com.safetynet.dto.FireAddress;
import com.safetynet.service.FireAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FireAddressServiceTest {

    @Mock
    private FireAddressService fireAddressService;

    @InjectMocks
    private FireAddressController fireAddressController;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void testGetFireAddress_ValidAddress_WithResidents() throws IOException {
        // Simule une réponse avec des habitants
        List<FireAddress> fireAddresses = List.of(
                new FireAddress("John Doe", "123-456-7890", 30, List.of("med1"), List.of("allergy1"), 1),
                new FireAddress("Jane Doe", "123-456-7891", 25, List.of("med2"), List.of("allergy2"), 1));
        when(fireAddressService.getFireAddress("123 Main St")).thenReturn(fireAddresses);

        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertEquals(2, response.getBody().size(), "La liste doit contenir 2 habitants");
        verify(fireAddressService).getFireAddress("123 Main St");
    }

    @Test
    void testGetFireAddress_ValidAddress_NoResidents() throws IOException {
        // Simule une réponse vide (aucun habitant)
        when(fireAddressService.getFireAddress("123 Main St")).thenReturn(Collections.emptyList());

        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(fireAddressService).getFireAddress("123 Main St");
    }

    @Test
    void testGetFireAddress_InvalidAddress() throws IOException {
        // Appelle la méthode avec une adresse invalide
        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress("");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(fireAddressService);
    }

    @Test
    void testGetFireAddress_NullAddress() throws IOException {
        // Appelle la méthode avec une adresse nulle
        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress(null);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(fireAddressService);
    }
}
