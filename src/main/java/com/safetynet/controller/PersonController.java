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

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    private void validPersons(List<Person> persons) {
        if (persons.isEmpty()) {
            log.warn("La liste des personnes est vide !");
            throw new IllegalArgumentException("La liste des personnes ne peut pas être vide.");
        }
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> creer(@Valid @RequestBody List<Person> persons) throws IOException {

        validPersons(persons);
        log.info("Personnes ajoutées avec succès.");
        return personService.add(persons);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> mettreAJour(@Valid @RequestBody List<Person> persons) throws IOException {

        validPersons(persons);
        log.info("Personnes mise à jour avec succès.");
        return personService.update(persons);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> supprimer(@Valid @RequestBody List<Person> persons) throws IOException {

        validPersons(persons);
        log.info("Personnes supprimées avec succès.");
        return personService.delete(persons);
    }

}
