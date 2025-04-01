package com.safetynet.communityEmail;

import com.safetynet.controller.CommunityEmailController;
import com.safetynet.dto.CommunityEmail;
import com.safetynet.service.CommunityEmailService;
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

class CommunityEmailControllerTest {

    @Mock
    private CommunityEmailService communityEmailService;

    @InjectMocks
    private CommunityEmailController communityEmailController;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void testGetCommunityEmail_ValidCity_WithEmails() throws IOException {
        // Simule une réponse avec des emails
        List<CommunityEmail> emails = List.of(
                new CommunityEmail("john.doe@email.com"),
                new CommunityEmail("jane.doe@email.com"));
        when(communityEmailService.getCommunityEmail("City1")).thenReturn(emails);

        // Appelle la méthode
        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail("City1");

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertEquals(2, response.getBody().size(), "La liste doit contenir 2 emails");
        verify(communityEmailService).getCommunityEmail("City1");
    }

    @Test
    void testGetCommunityEmail_ValidCity_NoEmails() throws IOException {
        // Simule une réponse vide (aucun email)
        when(communityEmailService.getCommunityEmail("City2")).thenReturn(Collections.emptyList());

        // Appelle la méthode
        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail("City2");

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(communityEmailService).getCommunityEmail("City2");
    }

    @Test
    void testGetCommunityEmail_InvalidCity() throws IOException {
        // Appelle la méthode avec une ville invalide
        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail("");

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(communityEmailService); // Le service ne doit pas être appelé
    }

    @Test
    void testGetCommunityEmail_NullCity() throws IOException {
        // Appelle la méthode avec une ville null
        ResponseEntity<List<CommunityEmail>> response = communityEmailController.getCommunityEmail(null);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(communityEmailService); // Le service ne doit pas être appelé
    }
}
