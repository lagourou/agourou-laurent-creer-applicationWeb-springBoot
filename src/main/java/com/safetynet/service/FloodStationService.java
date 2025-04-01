package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FloodStation;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FloodStationService {
        private final DataLoad dataLoad;
        private final AgeCalculationService ageCalculationService;

        public int getAge(String birthdate) {
                return ageCalculationService.calculateAge(birthdate);
        }

        public FloodStationService(DataLoad dataLoad, AgeCalculationService ageCalculationService) {
                this.dataLoad = dataLoad;
                this.ageCalculationService = ageCalculationService;
        }

        public Map<String, List<FloodStation>> getFloodStation(List<Integer> stationNumbers) throws IOException {
                List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
                });
                List<Firestation> firestations = dataLoad.readJsonFile("firestations",
                                new TypeReference<Map<String, List<Firestation>>>() {
                                });
                List<MedicalRecord> medicalRecords = dataLoad.readJsonFile("medicalrecords",
                                new TypeReference<Map<String, List<MedicalRecord>>>() {
                                });

                List<FloodStation> floodStations = persons.stream()
                                .filter(person -> {
                                        List<Integer> stationNumber = firestations.stream()
                                                        .filter(firestation -> firestation.getAddress().trim()
                                                                        .equalsIgnoreCase(person.getAddress().trim()))
                                                        .map(Firestation::getStation).collect(Collectors.toList());

                                        return stationNumber.stream().anyMatch(stationNumbers::contains);
                                })
                                .map(person -> {
                                        String name = person.getFirstName() + " " + person.getLastName();
                                        MedicalRecord record = medicalRecords.stream()
                                                        .filter(medical -> medical.getFirstName().trim()
                                                                        .equalsIgnoreCase(person.getFirstName().trim())
                                                                        && medical.getLastName().trim()
                                                                                        .equalsIgnoreCase(person
                                                                                                        .getLastName()
                                                                                                        .trim()))
                                                        .findFirst().orElse(null);

                                        int age = 0;
                                        List<String> medications = List.of();
                                        List<String> allergies = List.of();

                                        if (record != null) {
                                                log.info("Dossier médical trouvé pour : {} {}", person.getFirstName(),
                                                                person.getLastName());

                                                age = getAge(record.getBirthdate());
                                                medications = record.getMedications();
                                                allergies = record.getAllergies();
                                        } else {
                                                log.info("Dossier médical introuvable pour : {} {}",
                                                                person.getFirstName(), person.getLastName());
                                        }
                                        return new FloodStation(name, person.getPhone(), age, medications, allergies,
                                                        person.getAddress());

                                }).collect(Collectors.toList());

                return floodStations.stream().collect(Collectors.groupingBy(FloodStation::address));
        }

}
