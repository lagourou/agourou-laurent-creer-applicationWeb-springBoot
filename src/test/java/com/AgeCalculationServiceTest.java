package com;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.safetynet.service.AgeCalculationService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

class AgeCalculationServiceTest {

    private AgeCalculationService ageCalculationService;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        ageCalculationService = new AgeCalculationService();
    }

    @Test
    void testCalculateAge_ValidDate() {
        // Test avec une date valide
        String birthdate = "01/01/1990";
        int age = ageCalculationService.calculateAge(birthdate);

        // Calcul attendu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);
        int expectedAge = Period.between(birthDate, LocalDate.now()).getYears();

        assertEquals(expectedAge, age, "L'âge calculé devrait être correct");
    }

    @Test
    void testCalculateAge_EmptyDate() {
        // Test avec une date vide
        String birthdate = "";
        int age = ageCalculationService.calculateAge(birthdate);

        assertEquals(0, age, "L'âge devrait être 0 pour une date de naissance vide");
    }

    @Test
    void testCalculateAge_NullDate() {
        // Test avec une date nulle
        String birthdate = null;
        int age = ageCalculationService.calculateAge(birthdate);

        assertEquals(0, age, "L'âge devrait être 0 pour une date de naissance nulle");
    }

    @Test
    void testCalculateAge_InvalidFormat() {
        // Test avec une date au format incorrect
        String birthdate = "1990-01-01"; // Format invalide pour le pattern MM/dd/yyyy

        Exception exception = assertThrows(Exception.class, () -> ageCalculationService.calculateAge(birthdate));
        assertTrue(exception.getMessage().contains("Text '1990-01-01' could not be parsed"),
                "Une exception doit être levée pour les formats incorrects");
    }
}
