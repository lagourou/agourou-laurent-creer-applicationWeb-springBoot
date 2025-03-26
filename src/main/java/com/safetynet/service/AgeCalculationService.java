package com.safetynet.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AgeCalculationService {

    public int calculateAge(String birthdate) {
        if (birthdate == null || birthdate.isEmpty()) {
            log.warn("Date de naissance manquante ou vide !");
            return 0;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birthDate = LocalDate.parse(birthdate, formatter);
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            log.info("Date de naissance : {}, Âge calculé = {}", birthdate, age);
            return age;
        } catch (Exception e) {
            log.error("Erreur lors du parsing de la date : {}", birthdate, e);
            return 0; // Retourne 0 si le parsing échoue
        }
    }

}
