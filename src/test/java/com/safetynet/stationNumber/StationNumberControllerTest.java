package com.safetynet.stationNumber;

import com.safetynet.controller.StationNumberController;
import com.safetynet.dto.FirestationByPerson;
import com.safetynet.service.StationNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.safetynet.dto.PersonByStation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StationNumberControllerTest {

    @Mock
    private StationNumberService stationNumberService;

    @InjectMocks
    private StationNumberController stationNumberController;

    private FirestationByPerson firestationByPerson;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);

        firestationByPerson = new FirestationByPerson(
                List.of(new PersonByStation("John", "Doe", "123 Main St", "123-456-7890")),
                1,
                0);

    }

    @SuppressWarnings("null")
    @Test
    void testGetPersonsByStation_ValidStation() throws IOException {
        // Simule une réponse valide pour une station existante
        when(stationNumberService.getPersonByStation(1)).thenReturn(firestationByPerson);

        ResponseEntity<FirestationByPerson> response = stationNumberController.getPersonsByStation(1);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");

        if (response.getBody() != null) {
            assertEquals(1, response.getBody().numberOfAdults(), "Il doit y avoir 1 adulte pour la caserne 1");
            assertEquals(0, response.getBody().numberOfChildren(),
                    "Il ne doit pas y avoir d'enfants pour la caserne 1");
        }
        verify(stationNumberService).getPersonByStation(1);
    }

    @Test
    void testGetPersonsByStation_InvalidStationNumber() throws IOException {
        // Appelle la méthode avec un numéro de station invalide
        ResponseEntity<FirestationByPerson> response = stationNumberController.getPersonsByStation(-1);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(stationNumberService);
    }

    @Test
    void testGetPersonsByStation_StationWithNoData() throws IOException {
        // Simule une réponse vide pour une station existante mais sans données
        FirestationByPerson emptyResponse = new FirestationByPerson(Collections.emptyList(), 0, 0);
        when(stationNumberService.getPersonByStation(2)).thenReturn(emptyResponse);

        ResponseEntity<FirestationByPerson> response = stationNumberController.getPersonsByStation(2);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(stationNumberService).getPersonByStation(2);
    }

    @Test
    void testGetPersonsByStation_ValidStation_ServiceThrowsException() throws IOException {
        // Simule une exception lors de l'appel au service
        when(stationNumberService.getPersonByStation(1)).thenThrow(new IOException("Erreur de lecture des données"));

        Exception exception = assertThrows(IOException.class, () -> {
            stationNumberController.getPersonsByStation(1);
        });

        // Vérifie que l'exception est levée
        assertEquals("Erreur de lecture des données", exception.getMessage());
        verify(stationNumberService).getPersonByStation(1);
    }
}
