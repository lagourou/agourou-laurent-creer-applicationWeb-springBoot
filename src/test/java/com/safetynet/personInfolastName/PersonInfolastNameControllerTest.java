package com.safetynet.personInfolastName;

import com.safetynet.controller.PersonInfolastNameController;
import com.safetynet.dto.PersonInfolastName;
import com.safetynet.service.PersonInfolastNameService;
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
 * Tests pour le contrôleur PersonInfolastName.
 */
@ExtendWith(MockitoExtension.class)
class PersonInfolastNameControllerTest {

    @Mock
    private PersonInfolastNameService personInfolastNameService;

    @InjectMocks
    private PersonInfolastNameController personInfolastNameController;

    /**
     * Teste la recherche d'informations avec un nom de famille valide avec
     * résultats.
     * Vérifie que la réponse contient les informations attendues.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @SuppressWarnings("null")
    @Test
    void testGetPersonInfolastName_ValidLastName_WithResults() throws IOException {
        List<PersonInfolastName> persons = List.of(
                new PersonInfolastName("Doe", "123 Main St", 30, "john.doe@email.com", List.of("med1"),
                        List.of("allergy1")),
                new PersonInfolastName("Doe", "456 Oak St", 25, "jane.doe@email.com", List.of("med2"),
                        List.of("allergy2")));
        when(personInfolastNameService.getPersonInfolastName("Doe")).thenReturn(persons);

        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName("Doe");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertEquals(2, response.getBody().size(), "La liste doit contenir 2 personnes");
        verify(personInfolastNameService).getPersonInfolastName("Doe");
    }

    /**
     * Teste la recherche d'informations avec un nom de famille valide sans
     * résultats.
     * Vérifie que la réponse a un statut HTTP 204 et que le corps est null.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPersonInfolastName_ValidLastName_NoResults() throws IOException {
        when(personInfolastNameService.getPersonInfolastName("Smith")).thenReturn(Collections.emptyList());

        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName("Smith");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(personInfolastNameService).getPersonInfolastName("Smith");
    }

    /**
     * Teste la recherche d'informations avec un nom de famille invalide.
     * Vérifie que la réponse a un statut HTTP 400 et que le corps est nulle.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPersonInfolastName_InvalidLastName() throws IOException {
        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName("");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(personInfolastNameService);
    }

    /**
     * Teste la recherche d'informations avec un nom de famille nulle.
     * Vérifie que la réponse a un statut HTTP 400 et que le corps est nulle.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPersonInfolastName_NullLastName() throws IOException {
        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName(null);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(personInfolastNameService);
    }
}
