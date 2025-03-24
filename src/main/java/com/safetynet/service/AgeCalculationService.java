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
        log.info("Requête reçue pour calculer l'âge avec la date de naissance: {}", birthdate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        log.info("Age calculé retourné = {}", age);

        return age;
    }
}
