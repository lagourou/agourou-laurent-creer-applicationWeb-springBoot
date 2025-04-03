package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.CommunityEmail;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de récupérer les adresses email des résidents d'une ville
 * donnée.
 */
@Slf4j
@Service
public class CommunityEmailService {
    private final DataLoad dataLoad;

    /**
     * Constructeur de la classe CommunityEmailService.
     *
     * @param dataLoad Service pour charger les données depuis un fichier JSON.
     */

    public CommunityEmailService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    /**
     * Récupère les adresses email des résidents d'une ville donnée.
     *
     * @param city Ville regroupant ses habitants et ses adresses e-mails..
     * @return Une liste d'adresses email des résidents.
     * @throws IOException En cas d'erreur lors de la lecture des fichiers JSON.
     */
    public List<CommunityEmail> getCommunityEmail(String city) throws IOException {
        log.debug("Début de la récupération des emails pour la ville : {}", city);
        List<Person> persons;
        try {
            persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
            });
            log.debug("Données des personnes chargées depuis le fichier JSON : {}", persons);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier JSON pour la ville {}: {}", city, e.getMessage());
            throw e;
        }

        List<CommunityEmail> emails = persons.stream().filter(person -> person.getCity().trim().equalsIgnoreCase(city))
                .map(person -> new CommunityEmail(person.getEmail())).toList();

        log.debug("Emails filtrés pour la ville {}: {}", city, emails);
        log.info("{} Emails trouvés pour la ville {}", emails.size(), city);
        return emails;
    }

}
