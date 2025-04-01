package com.safetynet.personInfolastName;

import com.safetynet.controller.PersonInfolastNameController;
import com.safetynet.dto.PersonInfolastName;
import com.safetynet.service.PersonInfolastNameService;
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

class PersonInfolastNameControllerTest {

    @Mock
    private PersonInfolastNameService personInfolastNameService;

    @InjectMocks
    private PersonInfolastNameController personInfolastNameController;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void testGetPersonInfolastName_ValidLastName_WithResults() throws IOException {
        // Simule une réponse avec des personnes
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

    @Test
    void testGetPersonInfolastName_ValidLastName_NoResults() throws IOException {
        // Simule une réponse vide
        when(personInfolastNameService.getPersonInfolastName("Smith")).thenReturn(Collections.emptyList());

        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName("Smith");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(personInfolastNameService).getPersonInfolastName("Smith");
    }

    @Test
    void testGetPersonInfolastName_InvalidLastName() throws IOException {
        // Appelle la méthode avec un nom de famille invalide (chaîne vide)
        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName("");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(personInfolastNameService);
    }

    @Test
    void testGetPersonInfolastName_NullLastName() throws IOException {
        // Appelle la méthode avec un nom de famille nulle
        ResponseEntity<List<PersonInfolastName>> response = personInfolastNameController.getPersonInfolastName(null);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(personInfolastNameService);
    }
}
