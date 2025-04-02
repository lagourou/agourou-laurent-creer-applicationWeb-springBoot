package com.safetynet.phoneAlert;

import com.safetynet.controller.PhoneAlertController;
import com.safetynet.dto.PhoneAlert;
import com.safetynet.service.PhoneAlertService;

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
 * Tests pour le contrôleur PhoneAlert.
 */
@ExtendWith(MockitoExtension.class)
class PhoneAlertControllerTest {

    @Mock
    private PhoneAlertService phoneAlertService;

    @InjectMocks
    private PhoneAlertController phoneAlertController;

    /**
     * Teste la récupération des alertes téléphoniques pour une caserne valide avec
     * des numéros disponibles.
     * Vérifie que la réponse contient les numéros de téléphone attendus et que le
     * statut HTTP est 200.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @SuppressWarnings("null")
    @Test
    void testGetPhoneAlert_ValidFirestation_WithNumbers() throws IOException {
        List<PhoneAlert> phoneAlerts = List.of(
                new PhoneAlert("123-456-7890"),
                new PhoneAlert("987-654-3210"));
        when(phoneAlertService.getPhoneAlert(1)).thenReturn(phoneAlerts);

        ResponseEntity<List<PhoneAlert>> response = phoneAlertController.getPhoneAlert(1);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertEquals(2, response.getBody().size(), "Il doit y avoir 2 numéros de téléphone");
        verify(phoneAlertService).getPhoneAlert(1);
    }

    /**
     * Teste la récupération des alertes téléphoniques pour une caserne valide sans
     * numéros disponibles.
     * Vérifie que le statut HTTP est 204 et que le corps de la réponse est nulle.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPhoneAlert_ValidFirestation_NoNumbers() throws IOException {
        when(phoneAlertService.getPhoneAlert(1)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PhoneAlert>> response = phoneAlertController.getPhoneAlert(1);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(phoneAlertService).getPhoneAlert(1);
    }

    /**
     * Teste la récupération des alertes téléphoniques avec un numéro de caserne
     * invalide.
     * Vérifie que le statut HTTP est 400 et que le corps de la réponse est nulle.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPhoneAlert_InvalidFirestation() throws IOException {
        ResponseEntity<List<PhoneAlert>> response = phoneAlertController.getPhoneAlert(-1);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(phoneAlertService);
    }

    /**
     * Teste la récupération des alertes téléphoniques avec un numéro de caserne
     * égal à zéro.
     * Vérifie que le statut HTTP est 400 et que le corps de la réponse est nulle.
     */
    @Test
    void testGetPhoneAlert_ZeroFirestationNumber() throws IOException {
        ResponseEntity<List<PhoneAlert>> response = phoneAlertController.getPhoneAlert(0);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(phoneAlertService);
    }
}
