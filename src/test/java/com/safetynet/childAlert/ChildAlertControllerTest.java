package com.safetynet.childAlert;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.safetynet.controller.ChildAlertController;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.service.ChildAlertService;

/**
 * Test du controlleur ChildAlert.
 */
@ExtendWith(MockitoExtension.class)
class ChildAlertControllerTest {

    @Mock
    private ChildAlertService childAlertService;

    @InjectMocks
    private ChildAlertController childAlertController;

    /**
     * Teste la récupération des habitants pour une adresse valide avec des enfants.
     *
     * @throws IOException gère l'erreur lors de la lecture
     */
    @SuppressWarnings("null")
    @Test
    void testGetChildrenByAddress_ValidAddress_WithChildren() throws IOException {
        List<ChildrenByAddress> children = List.of(
                new ChildrenByAddress("John", "Doe", 10, List.of()),
                new ChildrenByAddress("Jane", "Doe", 8, List.of()));
        when(childAlertService.getChildrenByAddress("123 Main St")).thenReturn(children);

        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertFalse(response.getBody().isEmpty(), "La liste ne doit pas être vide");
        verify(childAlertService).getChildrenByAddress("123 Main St");
    }

    /**
     * Teste la récupération des habitants pour une adresse valide sans enfants.
     *
     * @throws IOException gère l'erreur lors de la lecture
     */
    @Test
    void testGetChildrenByAddress_ValidAddress_NoChildren() throws IOException {
        when(childAlertService.getChildrenByAddress("123 Main St")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(childAlertService).getChildrenByAddress("123 Main St");
    }

    /**
     * Teste la récupération des enfants pour une adresse vide.
     *
     * @throws IOException gère l'erreur lors de la lecture
     */
    @Test
    void testGetChildrenByAddress_InvalidAddress() throws IOException {
        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress("");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(childAlertService);
    }

    /**
     * Teste la récupération des enfants pour une adresse nulle.
     *
     * @throws IOException gère l'erreur lors de la lecture
     */
    @Test
    void testGetChildrenByAddress_NullAddress() throws IOException {
        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress(null);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(childAlertService);
    }

}
