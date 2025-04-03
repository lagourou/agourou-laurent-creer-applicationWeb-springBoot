package com.safetynet.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Service permettant de calculer l'âge d'une personne à partir de sa date de
 * naissance.
 */
@Slf4j
@Service
public class AgeCalculationService {

    /**
     * Calcule l'âge en années à partir de la date de naissance.
     *
     * @param birthdate La date de naissance au format MM/dd/yyyy. Peut être null ou
     *                  vide.
     * @return L'âge, retourne 0 si la date de naissance est manquante ou
     *         invalide.
     */
    public int calculateAge(String birthdate) {
        log.debug("Début du calcul de l'âge à partir de la date de naissance: {}", birthdate);

        if (birthdate == null || birthdate.isEmpty()) {
            log.info("Date de naissance manquante ou vide !");
            return 0;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate birthDate = LocalDate.parse(birthdate, formatter);

        int age = Period.between(birthDate, LocalDate.now()).getYears();

        return age;
    }

}
