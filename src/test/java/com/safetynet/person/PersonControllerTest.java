package com.safetynet.person;

import com.safetynet.controller.PersonController;
import com.safetynet.model.Person;
import com.safetynet.service.PersonService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Tests pour le contrôleur Person.
 */
@ExtendWith(MockitoExtension.class)
public class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    /**
     * Teste la création d'une liste de personnes.
     * Vérifie que la réponse du contrôleur correspond à la liste créée.
     *
     * @throws IOException s'il y a une erreur lors de l'exécution du service.
     */
    @Test
    void create_shouldReturnCreatedResponse() throws IOException {
        List<Person> persons = List.of(
                new Person("John", "Doe", "123 Main St", "Cityville", "12345", "555-555-1234", "john.doe@email.com"));
        when(personService.add(persons)).thenReturn(persons);
        List<Person> result = personController.creer(persons);
        verify(personService).add(persons);
        assertThat(result).isEqualTo(persons);
    }

    /**
     * Teste la mise à jour d'une liste de personnes.
     * Vérifie que la réponse du contrôleur correspond à la liste mis à jour.
     *
     * @throws IOException s'il y a une erreur lors de l'exécution du service.
     */
    @Test
    void update_shouldReturnUpdatedResponse() throws IOException {
        List<Person> persons = List.of(
                new Person("Jane", "Doe", "456 Elm St", "Cityville", "67890", "555-555-5678", "jane.doe@email.com"));
        when(personService.update(persons)).thenReturn(persons);
        List<Person> result = personController.mettreAJour(persons);
        verify(personService).update(persons);
        assertThat(result).isEqualTo(persons);
    }

    /**
     * Teste la suppression d'une liste de personnes.
     * Vérifie que la réponse du contrôleur correspond à la liste supprimée.
     *
     * @throws IOException s'il y a une erreur lors de l'exécution du service.
     */
    @Test
    void delete_shouldReturnOkResponse() throws IOException {
        List<Person> persons = List.of(
                new Person("Jack", "Smith", "789 Pine St", "Cityville", "11223", "555-555-7890",
                        "jack.smith@email.com"));
        when(personService.delete(persons)).thenReturn(persons);
        List<Person> result = personController.supprimer(persons);
        verify(personService).delete(persons);
        assertThat(result).isEqualTo(persons);
    }

    /**
     * Teste la création d'une liste vide de personnes.
     * Vérifie que la réponse du contrôleur correspond à la liste vide qui a été
     * créée.
     *
     * Vérifie qu'une exception IllegalArgumentException est levée.
     */
    @Test
    void create_shouldThrowExceptionWhenListIsEmpty() {
        List<Person> emptyPersons = List.of();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> personController.creer(emptyPersons));
        assertEquals("La liste des personnes ne peut pas être vide.", exception.getMessage());
    }

    /**
     * Teste la mise à jour d'une liste vide de personnes.
     * Vérifie que la réponse du contrôleur correspond à la liste vide qui a été
     * mis à jour.
     *
     * Vérifie qu'une exception IllegalArgumentException est levée.
     */
    @Test
    void update_shouldThrowExceptionWhenListIsEmpty() {
        List<Person> emptyPersons = List.of();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> personController.mettreAJour(emptyPersons));
        assertEquals("La liste des personnes ne peut pas être vide.", exception.getMessage());
    }

    /**
     * Teste la suppression d'une liste vide de personnes.
     * Vérifie que la réponse du contrôleur correspond à la liste vide qui a été
     * supprimée.
     *
     * Vérifie qu'une exception IllegalArgumentException est levée.
     */
    @Test
    void delete_shouldThrowExceptionWhenListIsEmpty() {
        List<Person> emptyPersons = List.of();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> personController.supprimer(emptyPersons));
        assertEquals("La liste des personnes ne peut pas être vide.", exception.getMessage());
    }
}
