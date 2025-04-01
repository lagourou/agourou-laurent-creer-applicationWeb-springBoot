package com.safetynet.fireAddress;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FireAddress;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.AgeCalculationService;
import com.safetynet.service.FireAddressService;
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

class FireAddressServiceTest {

    @Mock
    private DataLoad dataLoad;

    @Mock
    private AgeCalculationService ageCalculationService;

    @InjectMocks
    private FireAddressService fireAddressService;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;
    private List<Firestation> firestations;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);

        persons = List.of(
                new Person("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@email.com"),
                new Person("Jane", "Doe", "123 Main St", "City", "12345", "123-456-7891", "jane.doe@email.com"));

        medicalRecords = List.of(
                new MedicalRecord("John", "Doe", "01/01/1990", List.of("med1", "med2"), List.of("allergy1")),
                new MedicalRecord("Jane", "Doe", "02/02/1995", List.of("med3"), List.of("allergy2")));

        firestations = List.of(
                new Firestation("123 Main St", 1),
                new Firestation("456 Oak St", 2));
    }

    @Test
    void testGetFireAddress_ValidAddress() throws IOException {
        // Configure les mocks
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);
        when(ageCalculationService.calculateAge("01/01/1990")).thenReturn(33);
        when(ageCalculationService.calculateAge("02/02/1995")).thenReturn(28);

        // Appelle la méthode
        List<FireAddress> fireAddresses = fireAddressService.getFireAddress("123 Main St");

        // Vérifications
        assertNotNull(fireAddresses, "La liste des adresses de secours ne doit pas être null");
        assertEquals(2, fireAddresses.size(), "La liste doit contenir 2 personnes");

        FireAddress firstPerson = fireAddresses.get(0);
        assertEquals("John Doe", firstPerson.name());
        assertEquals(33, firstPerson.age());
        assertEquals(1, firstPerson.stationNumber());
        assertTrue(firstPerson.medications().contains("med1"));
        assertTrue(firstPerson.allergies().contains("allergy1"));
    }

    @Test
    void testGetFireAddress_InvalidAddress() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        // Appelle la méthode avec une adresse invalide
        List<FireAddress> fireAddresses = fireAddressService.getFireAddress("Nonexistent Address");

        assertNotNull(fireAddresses, "La liste des adresses de secours ne doit pas être null");
        assertTrue(fireAddresses.isEmpty(), "La liste doit être vide pour une adresse inexistante");
    }

    @Test
    void testGetFireAddress_NoMedicalRecord() throws IOException {
        // Supprime les dossiers médicaux pour simuler une absence de données
        medicalRecords = Collections.emptyList();
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);

        List<FireAddress> fireAddresses = fireAddressService.getFireAddress("123 Main St");

        assertNotNull(fireAddresses, "La liste des adresses de secours ne doit pas être null");
        assertEquals(2, fireAddresses.size(), "La liste doit contenir 2 personnes");
        assertEquals(0, fireAddresses.get(0).age(), "L'âge doit être 0 si aucun dossier médical n'est disponible");
        assertTrue(fireAddresses.get(0).medications().isEmpty(), "La liste des médicaments doit être vide");
        assertTrue(fireAddresses.get(0).allergies().isEmpty(), "La liste des allergies doit être vide");
    }

    @Test
    void testGetFireAddress_NoFirestation() throws IOException {
        // Supprime les données de casernes pour simuler une absence
        firestations = Collections.emptyList();
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(dataLoad.readJsonFile(eq("firestations"), any(TypeReference.class))).thenReturn(firestations);
        when(ageCalculationService.calculateAge("01/01/1990")).thenReturn(33);

        List<FireAddress> fireAddresses = fireAddressService.getFireAddress("123 Main St");

        assertNotNull(fireAddresses, "La liste des adresses de secours ne doit pas être null");
        assertEquals(2, fireAddresses.size(), "La liste doit contenir 2 personnes");
        assertEquals(0, fireAddresses.get(0).stationNumber(),
                "Le numéro de station doit être 0 si aucune caserne n'est disponible");
    }
}
