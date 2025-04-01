package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;
import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de gérer les informations des personnes.
 */
@Slf4j
@Service
public class PersonService {

    private final DataLoad dataLoad;

    /**
     * Constructeur de la classe PersonService.
     *
     * @param dataLoad Service pour charger et sauvegarder les données du fichier
     *                 JSON.
     */
    public PersonService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    /**
     * Ajoute des personnes au fichier JSON.
     *
     * @param persons Liste des personnes à ajouter.
     * @return Les personnes ajoutées.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<Person> add(List<Person> persons) throws IOException {
        log.info("Ajout de personnes au fichier Json.");
        List<Person> existingPersons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });

        for (Person newPerson : persons) {
            if (existingPersons.stream()
                    .anyMatch(existing -> existing.getFirstName().equalsIgnoreCase(newPerson.getFirstName())
                            && existing.getLastName().equalsIgnoreCase(newPerson.getLastName()))) {

                log.info("La personne existe déjà: {}", newPerson.getFirstName(), newPerson.getLastName());
                continue;
            }
            existingPersons.add(newPerson);

        }
        dataLoad.writeJsonFile("persons", existingPersons);
        log.info("Ajout de personnes fait");
        return persons;
    }

    /**
     * Met à jour les informations des personnes existantes.
     *
     * @param persons Liste des personnes à mettre à jour.
     * @return Les personnes mises à jour.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<Person> update(List<Person> persons) throws IOException {
        log.info("Mise à jour des personnes.");

        List<Person> existingPersons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });

        for (Person person : persons) {
            for (Person existing : existingPersons) {
                if (existing.getFirstName().equalsIgnoreCase(person.getFirstName())
                        && existing.getLastName().equalsIgnoreCase(person.getLastName())) {
                    existing.setAddress(person.getAddress());
                    existing.setCity(person.getCity());
                    existing.setPhone(person.getPhone());
                    existing.setZip(person.getZip());
                    existing.setEmail(person.getEmail());

                    log.info("Personne mise à jour : {}", existing);
                }
            }
        }
        dataLoad.writeJsonFile("persons", existingPersons);
        return existingPersons;
    }

    /**
     * Supprime des personnes.
     *
     * @param persons Liste des personnes à supprimer.
     * @return Les personnes supprimées.
     * @throws IOException En cas d'erreur lors de la lecture ou de la sauvegarde
     *                     des données.
     */
    public List<Person> delete(List<Person> persons) throws IOException {
        log.info("Suppression de personnes.");

        List<Person> existingPersons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });

        existingPersons.removeIf(
                p -> persons.stream().anyMatch(person -> p.getFirstName().equalsIgnoreCase(person.getFirstName())
                        && p.getLastName().equalsIgnoreCase(person.getLastName())));

        dataLoad.writeJsonFile("persons", existingPersons);

        log.info("Personne supprimeé: {}", existingPersons);
        return persons;
    }

}
