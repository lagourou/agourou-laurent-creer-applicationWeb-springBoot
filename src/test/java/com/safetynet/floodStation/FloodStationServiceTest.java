package com.safetynet.floodStation;

import com.safetynet.controller.FireAddressController;
import com.safetynet.dto.FireAddress;
import com.safetynet.service.FireAddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le service FireAddress.
 */
@ExtendWith(MockitoExtension.class)
class FireAddressServiceTest {

    @Mock
    private FireAddressService fireAddressService;

    @InjectMocks
    private FireAddressController fireAddressController;

    /**
     * Teste la récupération des informations des habitants pour une adresse valide
     * avec des résidents.
     *
     * S'assure que le statut HTTP retourné est 200.
     *
     * @throws IOException en cas d'erreur lors de l'exécution du test.
     */
    @SuppressWarnings("null")
    @Test
    void testGetFireAddress_ValidAddress_WithResidents() throws IOException {
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

    /**
     * Teste la récupération des informations des habitants pour une adresse valide
     * sans résidents.
     *
     * S'assure que le statut HTTP retourné est 204.
     *
     * @throws IOException en cas d'erreur lors de l'exécution du test.
     */
    @Test
    void testGetFireAddress_ValidAddress_NoResidents() throws IOException {
        when(fireAddressService.getFireAddress("123 Main St")).thenReturn(Collections.emptyList());

        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(fireAddressService).getFireAddress("123 Main St");
    }

    /**
     * Teste la récupération des informations pour une adresse invalide.
     *
     * Vérifie qu'une requête avec une adresse invalide (chaîne vide) retourne un
     * statut HTTP 400 et un corps de réponse null.
     * S'assure que le service FireAddressService n'est pas appelé.
     *
     * @throws IOException en cas d'erreur lors de l'exécution du test.
     */
    @Test
    void testGetFireAddress_InvalidAddress() throws IOException {
        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress("");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(fireAddressService);
    }

    /**
     * Teste la récupération des informations pour une adresse nulle.
     *
     * Vérifie qu'une requête avec une adresse nulle retourne un statut HTTP 400
     * et un corps de réponse null.
     *
     * @throws IOException en cas d'erreur lors de l'exécution du test.
     */
    @Test
    void testGetFireAddress_NullAddress() throws IOException {
        ResponseEntity<List<FireAddress>> response = fireAddressController.getFireAddress(null);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(fireAddressService);
    }
}
