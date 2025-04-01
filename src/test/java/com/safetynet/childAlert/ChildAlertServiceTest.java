package com.safetynet.childAlert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.AgeCalculationService;
import com.safetynet.service.ChildAlertService;
import com.safetynet.service.dataService.DataLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChildAlertServiceTest {

    @Mock
    private AgeCalculationService ageCalculationService;

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private ChildAlertService childAlertService;

    private List<Person> persons;
    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);

        persons = List.of(
                new Person("John", "Doe", "123 Main St", "City", "12345", "123-456-7890", "john.doe@email.com"),
                new Person("Jane", "Doe", "123 Main St", "City", "12345", "123-456-7891", "jane.doe@email.com"),
                new Person("Adult", "Smith", "123 Main St", "City", "12345", "123-456-7892", "adult.smith@email.com"));

        medicalRecords = List.of(
                new MedicalRecord("John", "Doe", "01/01/2015", List.of("med1"), List.of("allergy1")),
                new MedicalRecord("Jane", "Doe", "02/02/2010", List.of("med2"), List.of("allergy2")),
                new MedicalRecord("Adult", "Smith", "03/03/1980", List.of("med3"), List.of("allergy3")));
    }

    @Test
    void testGetAge_ValidDate() {
        // Simule le calcul de l'âge
        when(ageCalculationService.calculateAge("01/01/2015")).thenReturn(8);

        int age = childAlertService.getAge("01/01/2015");

        assertEquals(8, age, "L'âge calculé devrait être correct");
    }

    @Test
    void testGetAge_NullDate() {
        // Test avec une date nulle
        when(ageCalculationService.calculateAge(null)).thenReturn(0);

        int age = childAlertService.getAge(null);

        assertEquals(0, age, "L'âge devrait être 0 pour une date de naissance nulle");
    }

    @Test
    void testGetChildrenByAddress_WithChildren() throws IOException {
        // Configure les données simulées
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(ageCalculationService.calculateAge("01/01/2015")).thenReturn(8);
        when(ageCalculationService.calculateAge("02/02/2010")).thenReturn(13);
        when(ageCalculationService.calculateAge("03/03/1980")).thenReturn(43);

        List<ChildrenByAddress> children = childAlertService.getChildrenByAddress("123 Main St");

        assertNotNull(children, "La liste des enfants ne doit pas être null");
        assertEquals(2, children.size(), "La liste des enfants doit contenir 2 enfants");
        assertEquals("John", children.get(0).firstName());
        assertEquals(8, children.get(0).age());
        assertEquals(2, children.get(0).otherMembers().size(), "John doit avoir 2 autres membres dans son foyer");
    }

    @Test
    void testGetChildrenByAddress_NoChildren() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);
        when(ageCalculationService.calculateAge("01/01/2015")).thenReturn(20);
        when(ageCalculationService.calculateAge("02/02/2010")).thenReturn(19);
        when(ageCalculationService.calculateAge("03/03/1980")).thenReturn(43);

        List<ChildrenByAddress> children = childAlertService.getChildrenByAddress("123 Main St");

        assertNotNull(children, "La liste des enfants ne doit pas être null");
        assertEquals(0, children.size(), "Il ne doit pas y avoir d'enfants dans la liste");
    }

    @Test
    void testGetChildrenByAddress_InvalidAddress() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);
        when(dataLoad.readJsonFile(eq("medicalrecords"), any(TypeReference.class))).thenReturn(medicalRecords);

        List<ChildrenByAddress> children = childAlertService.getChildrenByAddress("Nonexistent Address");

        assertNotNull(children, "La liste des enfants ne doit pas être null");
        assertEquals(0, children.size(), "Il ne doit pas y avoir d'enfants dans la liste pour une adresse inexistante");
    }
}
