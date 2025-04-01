package com.safetynet.phoneAlert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PhoneAlert;
import com.safetynet.model.Firestation;
import com.safetynet.model.Person;
import com.safetynet.service.PhoneAlertService;
import com.safetynet.service.dataService.DataLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PhoneAlertServiceTest {

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private PhoneAlertService phoneAlertService;

    private List<Person> persons;
    private List<Firestation> firestations;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);

        persons = List.of(
                new Person("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@email.com"),
                new Person("Jane", "Doe", "456 Oak St", "City", "12345", "987-654-3210", "jane.doe@email.com"),
                new Person("Bob", "Smith", "789 Pine St", "City", "54321", "456-789-0123", "bob.smith@email.com"));

        firestations = List.of(
                new Firestation("123 Main St", 1),
                new Firestation("456 Oak St", 2));
    }

    @Test
    void testGetPhoneAlert_ValidFirestation() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(1, result.size(), "Il doit y avoir 1 numéro de téléphone pour la caserne 1");
        assertEquals("123-456-7890", result.get(0).phone(), "Le numéro de téléphone doit être 123-456-7890");
    }

    @Test
    void testGetPhoneAlert_InvalidFirestation() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        // Appelle la méthode avec une caserne inexistante
        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(999);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide pour une caserne inexistante");
    }

    @Test
    void testGetPhoneAlert_NoFirestations() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(Collections.emptyList());

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide si aucune caserne n'est définie");
    }

    @Test
    void testGetPhoneAlert_NoPersons() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(Collections.emptyList());
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        List<PhoneAlert> result = phoneAlertService.getPhoneAlert(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide si aucune personne n'est définie");
    }
}
