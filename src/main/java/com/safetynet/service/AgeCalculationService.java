package com.safetynet.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class AgeCalculationService {

    public int calculateAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDate = LocalDate.parse(birthdate, formatter);

        return Period.between(birthDate, LocalDate.now()).getYears();
    }

}
