package com.safetynet.firestation;

import com.safetynet.controller.FirestationController;
import com.safetynet.model.Firestation;
import com.safetynet.service.FirestationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le contrôleur Firestation.
 */
@ExtendWith(MockitoExtension.class)
class FirestationControllerTest {

    @Mock
    private FirestationService firestationService;

    @InjectMocks
    private FirestationController firestationController;

    private final List<Firestation> firestations = List.of(
            new Firestation("Address 1", 1),
            new Firestation("Station 2", 2));

    /**
     * Teste la méthode de création de casernes avec une liste valide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testCreate() throws IOException {
        when(firestationService.add(firestations)).thenReturn(firestations);

        List<Firestation> result = firestationController.creer(firestations);

        verify(firestationService).add(firestations);
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 casernes");
        assertEquals("Address 1", result.get(0).getAddress());
        assertEquals(1, result.get(0).getStation());
    }

    /**
     * Teste la méthode de création de casernes avec une liste vide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testCreateWithEmptyList() {
        List<Firestation> emptyFirestations = List.of();

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> firestationController.creer(emptyFirestations));
        assertEquals("La liste des casernes ne peut pas être vide.", exception.getMessage());
    }

    /**
     * Teste la méthode de mise à jour des casernes avec une liste valide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testUpdate() throws IOException {
        when(firestationService.update(firestations)).thenReturn(firestations);

        List<Firestation> result = firestationController.mettreAJour(firestations);

        verify(firestationService).update(firestations);
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 casernes");
    }

    /**
     * Teste la méthode de mise à jour des casernes avec une liste vide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testUpdateWithEmptyList() {
        List<Firestation> emptyFirestations = List.of();

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> firestationController.mettreAJour(emptyFirestations));
        assertEquals("La liste des casernes ne peut pas être vide.", exception.getMessage());
    }

    /**
     * Teste la méthode de suppression des casernes avec une liste valide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testDelete() throws IOException {
        when(firestationService.delete(firestations)).thenReturn(firestations);

        List<Firestation> result = firestationController.supprimer(firestations);

        verify(firestationService).delete(firestations);
        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 casernes");
    }

    /**
     * Teste la méthode de suppression des casernes avec une liste vide.
     *
     * @throws IOException gère l'erreur lors de l'exécution du test.
     */
    @Test
    void testDeleteWithEmptyList() {
        List<Firestation> emptyFirestations = List.of();

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> firestationController.supprimer(emptyFirestations));
        assertEquals("La liste des casernes ne peut pas être vide.", exception.getMessage());
    }
}
