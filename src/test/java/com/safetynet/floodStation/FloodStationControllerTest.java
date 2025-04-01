package com.safetynet.floodStation;

import com.safetynet.controller.FloodStationController;
import com.safetynet.dto.FloodStation;
import com.safetynet.service.FloodStationService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FloodStationControllerTest {

        @Mock
        private FloodStationService floodStationService;

        @InjectMocks
        private FloodStationController floodStationController;

        @BeforeEach
        @SuppressWarnings(value = { "unused" })
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        void testGetFloodStation_ValidStations_WithResidents() throws IOException {
                // Simule une réponse avec des foyers
                Map<String, List<FloodStation>> floodStations = Map.of(
                                "123 Main St", List.of(
                                                new FloodStation("John Doe", "123-456-7890", 30, List.of("med1"),
                                                                List.of("allergy1"),
                                                                "123 Main St"),
                                                new FloodStation("Jane Doe", "123-456-7891", 25, List.of("med2"),
                                                                List.of("allergy2"),
                                                                "123 Main St")),
                                "456 Oak St", List.of(
                                                new FloodStation("Bob Smith", "123-456-7892", 40, List.of(), List.of(),
                                                                "456 Oak St")));
                when(floodStationService.getFloodStation(List.of(1, 2))).thenReturn(floodStations);

                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController
                                .getFloodStation(List.of(1, 2));

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
                assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
                assertEquals(2, response.getBody().size(), "La réponse doit contenir 2 adresses");
                verify(floodStationService).getFloodStation(List.of(1, 2)); // Vérifie l'appel du service
        }

        @Test
        void testGetFloodStation_ValidStations_NoResidents() throws IOException {
                // Simule une réponse vide
                when(floodStationService.getFloodStation(List.of(1, 2))).thenReturn(Collections.emptyMap());

                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController
                                .getFloodStation(List.of(1, 2));

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
                                "Le statut HTTP doit être 204 (No Content)");
                assertNull(response.getBody(), "Le corps de la réponse doit être null");
                verify(floodStationService).getFloodStation(List.of(1, 2)); // Vérifie l'appel du service
        }

        @Test
        void testGetFloodStation_InvalidStations() throws IOException {
                // Appelle la méthode avec une liste de casernes vide
                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController
                                .getFloodStation(Collections.emptyList());

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                                "Le statut HTTP doit être 400 (Bad Request)");
                assertNull(response.getBody(), "Le corps de la réponse doit être null");

                // Vérifiez que le service n'est pas appelé lorsque la liste est vide
                verify(floodStationService, times(0)).getFloodStation(Collections.emptyList()); // Le service ne doit
                                                                                                // pas être
                                                                                                // appelé
        }

        @Test
        void testGetFloodStation_NullStations() throws IOException {
                // Appelle la méthode avec une liste de casernes null
                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController.getFloodStation(null);

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                                "Le statut HTTP doit être 400 (Bad Request)");
                assertNull(response.getBody(), "Le corps de la réponse doit être null");

                // Vérifiez que le service n'est pas appelé lorsque la liste est null
                verify(floodStationService, times(0)).getFloodStation(null); // Le service ne doit pas être appelé
        }
}
