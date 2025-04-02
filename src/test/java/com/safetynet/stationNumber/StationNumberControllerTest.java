package com.safetynet.stationNumber;

import com.safetynet.controller.StationNumberController;
import com.safetynet.dto.FirestationByPerson;
import com.safetynet.service.StationNumberService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.safetynet.dto.PersonByStation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le contrôleur StationNumber.
 */
@ExtendWith(MockitoExtension.class)
class StationNumberControllerTest {

    @Mock
    private StationNumberService stationNumberService;

    @InjectMocks
    private StationNumberController stationNumberController;

    private final FirestationByPerson firestationByPerson = new FirestationByPerson(
            List.of(new PersonByStation("John", "Doe", "123 Main St", "123-456-7890")),
            1,
            0);

    /**
     * Teste la récupération des personnes pour une caserne existante avec des
     * données valides.
     * Vérifie que la réponse contient les informations attendues et que le statut
     * HTTP est 200.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @SuppressWarnings("null")
    @Test
    void testGetPersonsByStation_ValidStation() throws IOException {
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

    /**
     * Teste la récupération des personnes avec un numéro de caserne invalide.
     * Vérifie que le statut HTTP est 400 et que le corps de la réponse est nulle.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPersonsByStation_InvalidStationNumber() throws IOException {
        ResponseEntity<FirestationByPerson> response = stationNumberController.getPersonsByStation(-1);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(stationNumberService);
    }

    /**
     * Teste la récupération des personnes pour une caserne valide sans données
     * disponibles.
     * Vérifie que le statut HTTP est 204 et que le corps de la réponse est nulle.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPersonsByStation_StationWithNoData() throws IOException {
        FirestationByPerson emptyResponse = new FirestationByPerson(Collections.emptyList(), 0, 0);
        when(stationNumberService.getPersonByStation(2)).thenReturn(emptyResponse);

        ResponseEntity<FirestationByPerson> response = stationNumberController.getPersonsByStation(2);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(stationNumberService).getPersonByStation(2);
    }

    /**
     * Teste la récupération des personnes pour une caserne valide lorsque le
     * service lance une exception.
     * Vérifie que l'exception est correctement relevée.
     *
     * @throws IOException s'il y a une erreur lors de l'appel au service.
     */
    @Test
    void testGetPersonsByStation_ValidStation_ServiceThrowsException() throws IOException {
        when(stationNumberService.getPersonByStation(1)).thenThrow(new IOException("Erreur de lecture des données"));

        Exception exception = assertThrows(IOException.class, () -> {
            stationNumberController.getPersonsByStation(1);
        });

        assertEquals("Erreur de lecture des données", exception.getMessage());
        verify(stationNumberService).getPersonByStation(1);
    }
}
