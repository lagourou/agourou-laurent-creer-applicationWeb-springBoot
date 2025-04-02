package com.safetynet.communityEmail;

import com.safetynet.controller.CommunityEmailController;
import com.safetynet.dto.CommunityEmail;
import com.safetynet.service.CommunityEmailService;
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
 * Tests pour le contrôleur CommunityEmail.
 */
@ExtendWith(MockitoExtension.class)
class CommunityEmailControllerTest {

    @Mock
    private CommunityEmailService communityEmailService;

    @InjectMocks
    private CommunityEmailController communityEmailController;

    /**
     * Teste la récupération des emails pour une ville valide avec des emails.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @SuppressWarnings("null")
    @Test
    void testGetCommunityEmail_ValidCity_WithEmails() throws IOException {
        List<CommunityEmail> emails = List.of(
                new CommunityEmail("john.doe@email.com"),
                new CommunityEmail("jane.doe@email.com"));
        when(communityEmailService.getCommunityEmail("City1")).thenReturn(emails);

        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail("City1");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertEquals(2, response.getBody().size(), "La liste doit contenir 2 emails");
        verify(communityEmailService).getCommunityEmail("City1");
    }

    /**
     * Teste la récupération des emails pour une ville valide sans emails.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testGetCommunityEmail_ValidCity_NoEmails() throws IOException {
        when(communityEmailService.getCommunityEmail("City2")).thenReturn(Collections.emptyList());

        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail("City2");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(communityEmailService).getCommunityEmail("City2");
    }

    /**
     * Teste la récupération des emails pour une ville invalide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testGetCommunityEmail_InvalidCity() throws IOException {
        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail("");

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(communityEmailService); // Le service ne doit pas être appelé
    }

    /**
     * Teste la récupération des emails pour une ville nulle.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testGetCommunityEmail_NullCity() throws IOException {
        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail(null);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(communityEmailService); // Le service ne doit pas être appelé
    }
}
