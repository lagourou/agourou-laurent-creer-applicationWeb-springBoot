package com.safetynet.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.safetynet.model.Person;
import com.safetynet.service.PersonService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Contrôleur pour gérer les alertes liées aux personnes.
 * Endpoint pour ajouter, mettre à jour et supprimer des personnes.
 */
@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    /**
     * Constructeur de la classe PersonController
     *
     * @param personService Service permettant de gérer les alertes liées aux
     *                      personnes.
     *
     */
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Valide que la liste des personnes n'est pas vide.
     *
     * @param persons Liste des personnes à valider.
     * @throws IllegalArgumentException Si la liste est vide ou nulle.
     *
     */
    private void validPersons(List<Person> persons) {
        if (persons == null || persons.isEmpty()) {
            log.error("La liste des personnes est vide !");
            throw new IllegalArgumentException("La liste des personnes ne peut pas être vide.");
        }
    }

    /**
     * Ajoute des personnes.
     *
     * @param persons Liste des personnes à ajouter.
     * @return Les personnes ajoutées.
     */
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The person has been added successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect elements")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> creer(@Valid @RequestBody List<Person> persons) throws IOException {

        validPersons(persons);
        log.debug("Appel au service 'personService.add' avec la liste des personnes : {}", persons);
        List<Person> result = personService.add(persons);

        log.debug("Résultat du service d'ajout : {}", result);
        log.info("Personnes ajoutées avec succès.");
        return result;
    }

    /**
     * Met à jour des personnes qui existes.
     *
     * @param persons Liste des personnes à mettre à jour.
     * @return Les personnes mises à jour.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The person has been successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect elements.")
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> mettreAJour(@Valid @RequestBody List<Person> persons) throws IOException {

        validPersons(persons);
        log.debug("Appel au service 'personService.update' avec la liste des personnes : {}", persons);
        List<Person> result = personService.update(persons);

        log.debug("Résultat du service mise à jour : {}", result);
        log.info("Personnes mise à jour avec succès.");
        return result;
    }

    /**
     * Supprime des personnes.
     *
     * @param persons Liste des personnes à supprimer.
     * @return Les personnes supprimées.
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The persons have been successfully deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid query: missing or incorrect elements."),
    })
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> supprimer(@Valid @RequestBody List<Person> persons) throws IOException {

        validPersons(persons);
        log.debug("Appel au service 'personService.delete' avec la liste des personnes : {}", persons);
        List<Person> result = personService.delete(persons);

        log.debug("Résultat du service suppression : {}", result);
        log.info("Personnes supprimées avec succès.");
        return result;
    }

}
