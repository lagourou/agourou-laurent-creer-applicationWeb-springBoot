package com.safetynet.floodStation;

import com.safetynet.controller.FloodStationController;
import com.safetynet.dto.FloodStation;
import com.safetynet.service.FloodStationService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le contrôleur FloodStation.
 */
@ExtendWith(MockitoExtension.class)
class FloodStationControllerTest {

        @Mock
        private FloodStationService floodStationService;

        @InjectMocks
        private FloodStationController floodStationController;

        /**
         * Teste la récupération des informations des foyers pour une liste valide de
         * casernes avec résidents.
         *
         * @throws IOException en cas d'erreur de traitement des données.
         */
        @SuppressWarnings("null")
        @Test
        void testGetFloodStation_ValidStations_WithResidents() throws IOException {
                Map<String, List<FloodStation>> floodStations = Map.of(
                                "123 Main St", List.of(
                                                new FloodStation("John Doe", "123-456-7890", 30, List.of("aspirin"),
                                                                List.of("peanut"),
                                                                "123 Main St"),
                                                new FloodStation("Jane Doe", "123-456-7891", 25, List.of("doliprane"),
                                                                List.of("fish"),
                                                                "123 Main St")),
                                "456 Juice St", List.of(
                                                new FloodStation("Bob Smith", "123-456-7892", 40, List.of(), List.of(),
                                                                "456 Juice St")));
                when(floodStationService.getFloodStation(List.of(1, 2))).thenReturn(floodStations);

                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController
                                .getFloodStation(List.of(1, 2));

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200 (OK)");
                assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
                assertEquals(2, response.getBody().size(), "La réponse doit contenir 2 adresses");
                verify(floodStationService).getFloodStation(List.of(1, 2));
        }

        /**
         * Teste la récupération des informations pour une liste valide de casernes sans
         * résidents.
         *
         * @throws IOException en cas d'erreur de traitement des données.
         */
        @Test
        void testGetFloodStation_ValidStations_NoResidents() throws IOException {
                when(floodStationService.getFloodStation(List.of(1, 2))).thenReturn(Collections.emptyMap());

                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController
                                .getFloodStation(List.of(1, 2));

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
                                "Le statut HTTP doit être 204 (No Content)");
                assertNull(response.getBody(), "Le corps de la réponse doit être null");
                verify(floodStationService).getFloodStation(List.of(1, 2));
        }

        /**
         * Teste la récupération des informations pour une liste de casernes vide.
         * Vérifie qu'une requête invalide est retournée.
         *
         * @throws IOException en cas d'erreur de traitement des données.
         */
        @Test
        void testGetFloodStation_InvalidStations() throws IOException {
                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController
                                .getFloodStation(Collections.emptyList());

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                                "Le statut HTTP doit être 400 (Bad Request)");
                assertNull(response.getBody(), "Le corps de la réponse doit être null");

                verify(floodStationService, times(0)).getFloodStation(Collections.emptyList());
        }

        /**
         * Teste la récupération des informations pour une liste de casernes nulle.
         * Vérifie qu'une requête invalide est retournée.
         *
         * @throws IOException en cas d'erreur de traitement des données.
         */
        @Test
        void testGetFloodStation_NullStations() throws IOException {
                ResponseEntity<Map<String, List<FloodStation>>> response = floodStationController.getFloodStation(null);

                assertNotNull(response, "La réponse ne doit pas être null");
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                                "Le statut HTTP doit être 400 (Bad Request)");
                assertNull(response.getBody(), "Le corps de la réponse doit être null");

                verify(floodStationService, times(0)).getFloodStation(null);
        }
}
