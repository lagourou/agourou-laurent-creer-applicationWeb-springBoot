package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PersonInfolastName;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersonInfolastNameService {
    private final DataLoad dataLoad;
    private final AgeCalculationService ageCalculationService;

    public int getAge(String birthdate) {
        return ageCalculationService.calculateAge(birthdate);
    }

    public PersonInfolastNameService(DataLoad dataLoad, AgeCalculationService ageCalculationService) {
        this.dataLoad = dataLoad;
        this.ageCalculationService = ageCalculationService;
    }

    public List<PersonInfolastName> getPersonInfolastName(String lastNames) throws IOException {

        log.info("Requête reçue pour getFloodStation avec le nom de famille des personnes: {}", lastNames);

        List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });
        List<MedicalRecord> medicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });

        List<PersonInfolastName> personInfolastNames = persons.stream()
                .filter(person -> person.getLastName().trim().equalsIgnoreCase(lastNames))
                .map(person -> {
                    MedicalRecord record = medicalRecords.stream()
                            .filter(medical -> medical.getFirstName().trim()
                                    .equalsIgnoreCase(person.getFirstName().trim())
                                    && medical.getLastName().trim().equalsIgnoreCase(person.getLastName().trim()))
                            .findFirst().orElse(null);

                    int age = 0;
                    String email = person.getEmail();
                    List<String> medications = List.of();
                    List<String> allergies = List.of();
                    if (record != null) {

                        log.warn("Dossier médical introuvable pour: {} {}", person.getFirstName(),
                                person.getLastName());

                        age = getAge(record.getBirthdate());
                        medications = record.getMedications();
                        allergies = record.getAllergies();
                    }

                    return new PersonInfolastName(person.getLastName(), person.getAddress(), age, email,
                            medications, allergies);
                }).toList();

        return personInfolastNames;
    }

}
