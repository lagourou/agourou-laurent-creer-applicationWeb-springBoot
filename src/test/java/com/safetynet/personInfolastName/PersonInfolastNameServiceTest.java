package com.safetynet.personInfolastName;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PersonInfolastName;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.PersonInfolastNameService;
import com.safetynet.service.dataService.DataLoad;
import com.safetynet.util.AgeCalculatorUtil;

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
 * Tests pour le service PersonInfolastName.
 */
@ExtendWith(MockitoExtension.class)
class PersonInfolastNameServiceTest {

    @Mock
    private DataLoad dataLoad;

    @Mock
    private AgeCalculatorUtil ageCalculatorUtil;

    @InjectMocks
    private PersonInfolastNameService personInfolastNameService;

    private final List<Person> persons = List.of(
            new Person("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@email.com"),
            new Person("Jane", "Doe", "456 Oak St", "City", "12345", "987-654-3210", "jane.doe@email.com"),
            new Person("Alice", "Smith", "789 Pine St", "City", "54321", "456-789-0123", "alice.smith@email.com"));

    private List<MedicalRecord> medicalRecords = List.of(
            new MedicalRecord("John", "Doe", "01/01/1990", List.of("med1", "med2"), List.of("allergy1")),
            new MedicalRecord("Jane", "Doe", "02/02/1995", List.of("med3"), List.of("allergy2")),
            new MedicalRecord("Alice", "Smith", "03/03/1980", List.of("med4"), List.of("allergy3")));

    /**
     * Teste la recherche des informations d'une personne par un nom de famille
     * valide.
     * Vérifie que les informations sont corrects.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonInfolastName_ValidLastName() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(ageCalculatorUtil.calculateAge("01/01/1990")).thenReturn(33);
        when(ageCalculatorUtil.calculateAge("02/02/1995")).thenReturn(28);

        List<PersonInfolastName> result = personInfolastNameService.getPersonInfolastName("Doe");

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Il doit y avoir 2 personnes avec le nom de famille Doe");

        PersonInfolastName firstPerson = result.get(0);
        assertEquals("Doe", firstPerson.lastName());
        assertEquals("123 Main St", firstPerson.address());
        assertEquals(33, firstPerson.age());
        assertTrue(firstPerson.medications().contains("med1"));
        assertTrue(firstPerson.allergies().contains("allergy1"));
    }

    /**
     * Teste la recherche avec un nom de famille inexistant.
     * Vérifie que le résultat est vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonInfolastName_InvalidLastName() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);

        List<PersonInfolastName> result = personInfolastNameService.getPersonInfolastName("Nonexistent");

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide pour un nom de famille inexistant");
    }

    /**
     * Teste la recherche avec un nom de famille valide mais sans données médicales.
     * Vérifie que les résultats retournent des âges nuls et des listes vides pour
     * les médicaments et allergies.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonInfolastName_NoMedicalRecords() throws IOException {
        medicalRecords = Collections.emptyList();
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);

        List<PersonInfolastName> result = personInfolastNameService.getPersonInfolastName("Doe");

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.size(), "Il doit y avoir 2 personnes avec le nom de famille Doe");
        assertEquals(0, result.get(0).age(), "L'âge doit être 0 si aucun dossier médical n'est disponible");
        assertTrue(result.get(0).medications().isEmpty(), "Les médicaments doivent être vides");
        assertTrue(result.get(0).allergies().isEmpty(), "Les allergies doivent être vides");
    }

    /**
     * Teste la recherche avec un nom de famille nulle.
     * Vérifie que le résultat est vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonInfolastName_NullLastName() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);

        List<PersonInfolastName> result = personInfolastNameService.getPersonInfolastName(null);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.isEmpty(), "Le résultat doit être vide pour un nom de famille null");
    }
}
