package com.safetynet.firestation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.Firestation;
import com.safetynet.service.FirestationService;
import com.safetynet.service.dataService.DataLoad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le service Firestation.
 */
@ExtendWith(MockitoExtension.class)
public class FirestationServiceTest {

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private FirestationService firestationService;

    /**
     * Teste l'ajout d'une nouvelle caserne.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des données.
     */
    @Test
    void add_shouldAddNewFirestation() throws IOException {
        Firestation newFirestation = new Firestation("211 Red St", 1);
        List<Firestation> newFirestations = List.of(newFirestation);
        List<Firestation> existingFirestations = new ArrayList<>();

        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class)))
                .thenReturn(existingFirestations);

        List<Firestation> result = firestationService.add(newFirestations);

        ArgumentCaptor<List<Firestation>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("firestations"), captor.capture());

        List<Firestation> updatedList = captor.getValue();

        assertThat(updatedList).contains(newFirestation);
        assertThat(result).isEqualTo(newFirestations);
    }

    /**
     * Teste l'ajout d'une caserne déjà existante.
     * La liste finale ne doit pas contenir de doublons.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des données.
     */
    @Test
    void add_shouldSkipExistingFirestation() throws IOException {
        Firestation existingFirestation = new Firestation("1509 Culver St", 2);
        List<Firestation> newFirestations = List.of(existingFirestation);
        List<Firestation> existingFirestations = new ArrayList<>(List.of(existingFirestation));

        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class)))
                .thenReturn(existingFirestations);

        List<Firestation> result = firestationService.add(newFirestations);

        ArgumentCaptor<List<Firestation>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("firestations"), captor.capture());
        List<Firestation> updatedList = captor.getValue();

        assertEquals(existingFirestations, updatedList);
        assertThat(result).isEqualTo(newFirestations);
    }

    /**
     * Teste la mise à jour des informations d'une caserne existante.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des données.
     */
    @Test
    void update_shouldUpdateFirestation() throws IOException {
        Firestation existingFirestation = new Firestation("1509 Culver St", 2);
        List<Firestation> existingFirestations = new ArrayList<>(List.of(existingFirestation));

        Firestation updatedFirestation = new Firestation("1509 Culver St", 5);
        List<Firestation> updateFirestations = List.of(updatedFirestation);

        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class)))
                .thenReturn(existingFirestations);

        List<Firestation> result = firestationService.update(updateFirestations);

        ArgumentCaptor<List<Firestation>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("firestations"), captor.capture());
        List<Firestation> updatedList = captor.getValue();

        assertEquals(5, updatedList.get(0).getStation(), "Le numéro de la station doit être mis à jour.");
        assertThat(result).isEqualTo(existingFirestations);
    }

    /**
     * Teste la suppression d'une caserne existante.
     * La caserne supprimée ne doit plus apparaître dans la liste finale.
     *
     * @throws IOException en cas d'erreur de lecture/écriture des données.
     */
    @Test
    void delete_shouldRemoveFirestation() throws IOException {
        Firestation firestationToDelete = new Firestation("951 LoneTree Rd", 3);
        List<Firestation> existingFirestations = new ArrayList<>(List.of(firestationToDelete));
        List<Firestation> firestationsToDelete = List.of(firestationToDelete);

        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class)))
                .thenReturn(existingFirestations);

        List<Firestation> result = firestationService.delete(firestationsToDelete);

        ArgumentCaptor<List<Firestation>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataLoad).writeJsonFile(eq("firestations"), captor.capture());
        List<Firestation> updatedList = captor.getValue();

        assertThat(updatedList).doesNotContain(firestationToDelete);
        assertThat(result).isEqualTo(firestationsToDelete);
    }
}
