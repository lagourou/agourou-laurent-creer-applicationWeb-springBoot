package com.safetynet.firestation;

import com.safetynet.controller.FirestationController;
import com.safetynet.model.Firestation;
import com.safetynet.service.FirestationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirestationControllerTest {

    @Mock
    private FirestationService firestationService;

    @InjectMocks
    private FirestationController firestationController;

    private List<Firestation> firestations;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);
        firestations = List.of(
                new Firestation("Address 1", 1),
                new Firestation("Station 2", 2));
    }

    @Test
    void testCreer() throws IOException {
        when(firestationService.add(firestations)).thenReturn(firestations);

        List<Firestation> result = firestationController.creer(firestations);

        verify(firestationService).add(firestations);
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 casernes");
        assertEquals("Address 1", result.get(0).getAddress());
        assertEquals(1, result.get(0).getStation());
    }

    @Test
    void testCreerWithEmptyList() {
        // Configuration des données invalides
        List<Firestation> emptyFirestations = List.of();

        // Vérifie qu'une exception est levée
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> firestationController.creer(emptyFirestations));
        assertEquals("La liste des casernes ne peut pas être vide.", exception.getMessage());
    }

    @Test
    void testMettreAJour() throws IOException {
        // Configure le mock pour renvoyer les casernes mises à jour
        when(firestationService.update(firestations)).thenReturn(firestations);

        // Appel de la méthode
        List<Firestation> result = firestationController.mettreAJour(firestations);

        // Vérifications
        verify(firestationService).update(firestations);
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 casernes");
    }

    @Test
    void testMettreAJourWithEmptyList() {
        // Configuration des données invalides
        List<Firestation> emptyFirestations = List.of();

        // Vérifie qu'une exception est levée
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> firestationController.mettreAJour(emptyFirestations));
        assertEquals("La liste des casernes ne peut pas être vide.", exception.getMessage());
    }

    @Test
    void testSupprimer() throws IOException {
        // Configure le mock pour renvoyer les casernes supprimées
        when(firestationService.delete(firestations)).thenReturn(firestations);

        // Appel de la méthode
        List<Firestation> result = firestationController.supprimer(firestations);

        // Vérifications
        verify(firestationService).delete(firestations);
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 casernes");
    }

    @Test
    void testSupprimerWithEmptyList() {
        // Configuration des données invalides
        List<Firestation> emptyFirestations = List.of();

        // Vérifie qu'une exception est levée
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> firestationController.supprimer(emptyFirestations));
        assertEquals("La liste des casernes ne peut pas être vide.", exception.getMessage());
    }
}
