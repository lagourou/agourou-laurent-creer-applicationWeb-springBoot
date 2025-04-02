package com.safetynet.person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.safetynet.model.Person;
import com.safetynet.service.PersonService;
import com.safetynet.service.dataService.DataLoad;

/**
 * Tests pour le service Person.
 */
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
        @Mock
        private DataLoad dataLoad;

        @InjectMocks
        private PersonService personService;

        private final List<Person> existingPersons = List.of(
                        new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
                                        "jaboyd@email.com"),
                        new Person("Eric", "Cadigan", "951 LoneTree Rd", "Culver", "97451", "841-874-7458",
                                        "gramps@email.com"));

        /**
         * Teste l'ajout de nouvelles personnes qui n'existent pas dans la liste.
         * Vérifie que seules les nouvelles personnes sont ajoutées.
         *
         * @throws IOException s'il y a une erreur lors de l'appel au service.
         */
        @Test
        void addPersonNotExist() throws IOException {
                List<Person> newPersons = List.of(
                                new Person("Peter", "Parker", "211 Red St", "Queens", "25878", "821-774-6912",
                                                "parkerp@email.com"),
                                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
                                                "jaboyd@email.com"));

                List<Person> result = personService.add(newPersons);

                ArgumentCaptor<List<Person>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                verify(dataLoad).writeJsonFile(eq("persons"), argumentCaptor.capture());

                List<Person> updatedPersons = argumentCaptor.getValue();
                assertTrue(updatedPersons.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("Peter") &&
                                                person.getLastName().equalsIgnoreCase("Parker")),
                                "La nouvelle personne Peter Parker doit être ajoutée.");
                assertEquals(2, updatedPersons.size(), "La liste mise à jour doit contenir exactement 2 personnes.");

                assertNotNull(result, "Le résultat ne doit pas être null.");
                assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 personnes.");
                assertTrue(result.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("Peter") &&
                                                person.getLastName().equalsIgnoreCase("Parker")),
                                "Le résultat doit contenir Peter Parker.");
                assertTrue(result.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("John") &&
                                                person.getLastName().equalsIgnoreCase("Boyd")),
                                "Le résultat doit contenir les personnes déjà existantes.");
        }

        /**
         * Teste l'ajout des personnes qui existent déjà dans la liste.
         * Vérifie que la liste finale est identique à la liste qui existe déjà.
         *
         * @throws IOException s'il y a une erreur lors de l'appel au service.
         */
        @Test
        void addPersonExist() throws IOException {

                List<Person> result = personService.add(existingPersons);

                ArgumentCaptor<List<Person>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                verify(dataLoad).writeJsonFile(eq("persons"), argumentCaptor.capture());

                List<Person> updatedPersons = argumentCaptor.getValue();

                assertEquals(2, updatedPersons.size(), "La liste mise à jour doit contenir exactement 2 personnes.");
                assertTrue(updatedPersons.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("John") &&
                                                person.getLastName().equalsIgnoreCase("Boyd")),
                                "John Boyd doit être présent dans la liste.");
                assertTrue(updatedPersons.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("Eric") &&
                                                person.getLastName().equalsIgnoreCase("Cadigan")),
                                "Eric Cadigan doit être présent dans la liste.");
                assertNotNull(result, "Le résultat ne doit pas être null.");
                assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 personnes.");
                assertTrue(result.containsAll(existingPersons),
                                "Le résultat doit contenir toutes les personnes passées en paramètre.");
        }

        /**
         * Teste la mise à jour des personnes qui existent déjà dans la liste.
         * Vérifie que les informations de la personne existante sont bien modifiées.
         *
         * @throws IOException s'il y a une erreur lors de l'appel au service.
         */
        @Test
        void updateExistingPerson() throws IOException {
                when(dataLoad.readJsonFile(eq("persons"), any())).thenReturn(new ArrayList<>(existingPersons));

                List<Person> personsToUpdate = List.of(
                                new Person("John", "Boyd", "7875 Wall St", "New-York", "58614",
                                                "871-804-6519", "Bjohn.email@email.com"));

                @SuppressWarnings("unused")
                List<Person> updatedResult = personService.update(personsToUpdate);

                ArgumentCaptor<List<Person>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                verify(dataLoad).writeJsonFile(eq("persons"), argumentCaptor.capture());

                List<Person> savedPersons = argumentCaptor.getValue();

                Person updatedJohn = savedPersons.stream()
                                .filter(person -> person.getFirstName().equalsIgnoreCase("John")
                                                && person.getLastName().equalsIgnoreCase("Boyd"))
                                .findFirst()
                                .orElseThrow(() -> new AssertionError(
                                                "John Boyd doit être présent dans la liste mise à jour."));

                assertEquals("7875 Wall St", updatedJohn.getAddress(),
                                "L'adresse de John Boyd doit être mise à jour.");
                assertEquals("New-York", updatedJohn.getCity(), "La ville de John Boyd doit être mise à jour.");
                assertEquals("58614", updatedJohn.getZip(), "Le code postal de John Boyd doit être mis à jour.");
                assertEquals("871-804-6519", updatedJohn.getPhone(),
                                "Le numéro de téléphone de John Boyd doit être mis à jour.");
                assertEquals("Bjohn.email@email.com", updatedJohn.getEmail(),
                                "L'email de John Boyd doit être mis à jour.");

                assertEquals(2, savedPersons.size(), "La liste sauvegardée doit contenir exactement 2 personnes.");
        }

        /**
         * Teste la mise à jour des personnes qui n'existent pas dans la liste.
         * Vérifie que les informations de la personne ne sont pas modifiées.
         *
         * @throws IOException s'il y a une erreur lors de l'appel au service.
         */
        @Test
        void updateNonExistingPerson() throws IOException {
                List<Person> mockedExistingPersons = List.of(
                                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
                                                "jaboyd@email.com"),
                                new Person("Eric", "Cadigan", "951 LoneTree Rd", "Culver", "97451", "841-874-7458",
                                                "gramps@email.com"));
                when(dataLoad.readJsonFile(eq("persons"), any())).thenReturn(new ArrayList<>(mockedExistingPersons));

                List<Person> personsToUpdate = List.of(
                                new Person("Nonexistent", "Person", "Some Address", "Some City", "Some Zip",
                                                "Some Phone", "nonexistent@email.com"));

                List<Person> result = personService.update(personsToUpdate);

                ArgumentCaptor<List<Person>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                verify(dataLoad).writeJsonFile(eq("persons"), argumentCaptor.capture());

                List<Person> updatedPersons = argumentCaptor.getValue();

                assertEquals(2, updatedPersons.size(), "La liste mise à jour doit contenir exactement 2 personnes.");
                assertTrue(updatedPersons.containsAll(mockedExistingPersons),
                                "La liste mise à jour doit contenir toutes les personnes existantes.");
                assertEquals(2, result.size(), "Le résultat doit contenir exactement 2 personnes.");
                assertTrue(result.containsAll(mockedExistingPersons),
                                "Le résultat doit contenir toutes les personnes existantes.");
        }

        /**
         * Teste la suppression des personnes qui existent déjà dans la liste.
         * Vérifie que les informations de la personne existante sont bien supprimées.
         *
         * @throws IOException s'il y a une erreur lors de l'appel au service.
         */
        @Test
        void deleteExistingPerson() throws IOException {
                when(dataLoad.readJsonFile(eq("persons"), any())).thenReturn(new ArrayList<>(existingPersons));

                List<Person> personsToDelete = List.of(
                                new Person("John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
                                                "jaboyd@email.com"));

                List<Person> deletedPersons = personService.delete(personsToDelete);

                ArgumentCaptor<List<Person>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                verify(dataLoad).writeJsonFile(eq("persons"), argumentCaptor.capture());

                List<Person> updatedPersons = argumentCaptor.getValue();

                assertEquals(1, updatedPersons.size(), "Il ne doit rester qu'une seule personne après la suppression.");
                assertFalse(updatedPersons.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("John") &&
                                                person.getLastName().equalsIgnoreCase("Boyd")),
                                "John Boyd doit être supprimé de la liste.");

                assertNotNull(deletedPersons, "Le résultat ne doit pas être null.");
                assertEquals(1, deletedPersons.size(), "Une seule personne doit être supprimée.");
                assertTrue(deletedPersons.stream()
                                .anyMatch(person -> person.getFirstName().equalsIgnoreCase("John") &&
                                                person.getLastName().equalsIgnoreCase("Boyd")),
                                "John Boyd doit être dans la liste des personnes supprimées.");
        }

        /**
         * Teste la suppression des personnes qui n'existent pas dans la liste.
         * Vérifie que les informations de la personne ne sont pas supprimées.
         *
         * @throws IOException s'il y a une erreur lors de l'appel au service.
         */
        @Test
        void deleteNoExistingPerson() throws IOException {
                when(dataLoad.readJsonFile(eq("persons"), any())).thenReturn(new ArrayList<>(existingPersons));

                List<Person> personsToDelete = List.of(
                                new Person("Fiona", "Lake", "789 Green St", "Beverly", "27851", "041-774-6712",
                                                "fLake@email.com"));

                List<Person> deletedPersons = personService.delete(personsToDelete);

                ArgumentCaptor<List<Person>> argumentCaptor = ArgumentCaptor.forClass(List.class);
                verify(dataLoad).writeJsonFile(eq("persons"), argumentCaptor.capture());

                List<Person> updatedPersons = argumentCaptor.getValue();

                assertEquals(2, updatedPersons.size(), "La liste mise à jour doit contenir exactement 2 personnes.");
                assertTrue(updatedPersons.containsAll(existingPersons),
                                "Toutes les personnes existantes doivent rester inchangées.");

                assertNotNull(deletedPersons, "Le résultat ne doit pas être null.");
                assertTrue(personsToDelete.stream().noneMatch(person -> updatedPersons.stream()
                                .anyMatch(existing -> existing.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                                                existing.getLastName().equalsIgnoreCase(person.getLastName()))),
                                "Aucune personne inexistante ne doit être supprimée.");
        }

}
