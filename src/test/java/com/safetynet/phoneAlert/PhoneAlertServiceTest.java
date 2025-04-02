package com.safetynet.phoneAlert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PhoneAlert;
import com.safetynet.model.Firestation;
import com.safetynet.model.Person;
import com.safetynet.service.PhoneAlertService;
import com.safetynet.service.dataService.DataLoad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests pour le service PhoneAlert.
 */
@ExtendWith(MockitoExtension.class)
class PhoneAlertServiceTest {

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private PhoneAlertService phoneAlertService;

    private final List<Person> persons = List.of(
            new Person("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@email.com"),
            new Person("Jane", "Doe", "456 Oak St", "City", "12345", "987-654-3210", "jane.doe@email.com"),
            new Person("Bob", "Smith", "789 Pine St", "City", "54321", "456-789-0123", "bob.smith@email.com"));

    private final List<Firestation> firestations = List.of(
            new Firestation("123 Main St", 1),
            new Firestation("456 Oak St", 2));

    /**
     * Teste la récupération des numéros de téléphone pour une caserne valide.
     * Vérifie que le service retourne les numéros liés à la caserne et que le
     * résultat est correct.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPhoneAlert_ValidFirestation() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(1, result.size(), "Il doit y avoir 1 numéro de téléphone pour la caserne 1");
        assertEquals("123-456-7890", result.get(0).phone(), "Le numéro de téléphone doit être 123-456-7890");
    }

    /**
     * Teste la récupération des numéros de téléphone pour une caserne inexistante.
     * Vérifie que le service retourne une liste vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPhoneAlert_InvalidFirestation() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(999);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide pour une caserne inexistante");
    }

    /**
     * Teste la récupération des numéros de téléphone lorsque aucune caserne n'est
     * définie.
     * Vérifie que le service retourne une liste vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPhoneAlert_NoFirestations() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(Collections.emptyList());

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide si aucune caserne n'est définie");
    }

    /**
     * Teste la récupération des numéros de téléphone lorsque aucune personne n'est
     * définie.
     * Vérifie que le service retourne une liste vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPhoneAlert_NoPersons() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(Collections.emptyList());
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide si aucune personne n'est définie");
    }
}
