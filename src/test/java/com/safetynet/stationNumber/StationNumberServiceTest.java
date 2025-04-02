package com.safetynet.stationNumber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FirestationByPerson;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.AgeCalculationService;
import com.safetynet.service.StationNumberService;
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
 * Tests pour le service StationNumber.
 */
@ExtendWith(MockitoExtension.class)
class StationNumberServiceTest {

    @Mock
    private DataLoad dataLoad;

    @Mock
    private AgeCalculationService ageCalculationService;

    @InjectMocks
    private StationNumberService stationNumberService;

    private final List<Person> persons = List.of(
            new Person("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@email.com"),
            new Person("Jane", "Doe", "123 Main St", "City", "12345", "987-654-3210", "jane.doe@email.com"),
            new Person("Bob", "Smith", "456 Oak St", "City", "54321", "456-789-0123", "bob.smith@email.com"));

    private List<Firestation> firestations = List.of(
            new Firestation("123 Main St", 1),
            new Firestation("456 Oak St", 2));

    private List<MedicalRecord> medicalRecords = List.of(
            new MedicalRecord("John", "Doe", "01/01/1990", List.of("med1", "med2"), List.of("allergy1")),
            new MedicalRecord("Jane", "Doe", "02/02/1995", List.of("med3"), List.of("allergy2")),
            new MedicalRecord("Bob", "Smith", "03/03/2010", List.of("med4"), List.of("allergy3")));

    /**
     * Teste la récupération des informations des personnes pour un numéro de
     * caserne valide.
     * Vérifie que le service retourne les informations attendues.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonByStation_ValidStation() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(ageCalculationService.calculateAge("01/01/1990")).thenReturn(33); // Adulte
        when(ageCalculationService.calculateAge("02/02/1995")).thenReturn(28); // Adulte

        FirestationByPerson result = stationNumberService.getPersonByStation(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.persons().size(), "Il doit y avoir 2 personnes associées à la caserne 1");
        assertEquals(2, result.numberOfAdults(), "Il doit y avoir 2 adultes pour la caserne 1");
        assertEquals(0, result.numberOfChildren(), "Il doit y avoir 0 enfants pour la caserne 1");
    }

    /**
     * Teste la récupération des informations pour un numéro de caserne inexistant.
     * Vérifie que le service retourne un résultat vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonByStation_InvalidStation() throws IOException {

        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        FirestationByPerson result = stationNumberService.getPersonByStation(999);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.persons().isEmpty(), "La liste des personnes doit être vide pour une station inexistante");
        assertEquals(0, result.numberOfAdults(), "Il ne doit pas y avoir d'adultes pour une station inexistante");
        assertEquals(0, result.numberOfChildren(), "Il ne doit pas y avoir d'enfants pour une station inexistante");
    }

    /**
     * Teste la récupération des informations lorsque les dossiers médicaux sont
     * absents.
     * Vérifie que le service considère toutes les personnes comme enfants.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonByStation_NoMedicalRecords() throws IOException {
        medicalRecords = Collections.emptyList();
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);

        verifyNoInteractions(ageCalculationService);

        FirestationByPerson result = stationNumberService.getPersonByStation(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertEquals(2, result.persons().size(), "Il doit y avoir 2 personnes associées à la caserne 1");

        assertEquals(0, result.numberOfAdults(),
                "Il ne doit pas y avoir d'adultes car aucun âge ne peut être déterminé");
        assertEquals(2, result.numberOfChildren(),
                "Toutes les personnes sont considérées comme enfants faute de données médicales");
    }

    /**
     * Teste la récupération des informations lorsque les casernes sont absentes.
     * Vérifie que le service retourne un résultat vide.
     *
     * @throws IOException s'il y a une erreur lors de l'accès aux données.
     */
    @Test
    void testGetPersonByStation_NoFirestations() throws IOException {
        firestations = Collections.emptyList();
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        FirestationByPerson result = stationNumberService.getPersonByStation(1);

        assertNotNull(result, "Le résultat ne doit pas être null");
        assertTrue(result.persons().isEmpty(), "Le résultat doit être vide si aucune caserne n'est définie");
        assertEquals(0, result.numberOfAdults(), "Il ne doit pas y avoir d'adultes si aucune caserne n'est définie");
        assertEquals(0, result.numberOfChildren(), "Il ne doit pas y avoir d'enfants si aucune caserne n'est définie");
    }

}
