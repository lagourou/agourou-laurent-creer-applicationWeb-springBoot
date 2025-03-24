package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.FirestationByPerson;
import com.safetynet.dto.PersonByStation;
import com.safetynet.model.Firestation;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StationNumberService {
        private final AgeCalculationService ageCalculationService;
        private final DataLoad dataLoad;

        public StationNumberService(AgeCalculationService ageCalculationService, DataLoad dataLoad) {
                this.ageCalculationService = ageCalculationService;
                this.dataLoad = dataLoad;
        }

        public int getAge(String birthdate) {
                return ageCalculationService.calculateAge(birthdate);
        }

        public FirestationByPerson getPersonByStation(int stationNumber) throws IOException {
                log.info("Requête reçue pour getPersonByStation avec le numéro de la caserne: {}", stationNumber);

                List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
                });
                List<Firestation> firestations = dataLoad.readJsonFile("firestations",
                                new TypeReference<Map<String, List<Firestation>>>() {
                                });
                List<MedicalRecord> medicalRecords = dataLoad.readJsonFile("medicalrecords",
                                new TypeReference<Map<String, List<MedicalRecord>>>() {
                                });

                List<String> address = firestations.stream()
                                .filter(firestation -> firestation.getStation() == stationNumber)
                                .map(Firestation::getAddress).toList();
                List<PersonByStation> filterPersonByStations = persons.stream()
                                .filter(person -> address.contains(person.getAddress()))
                                .map(person -> new PersonByStation(person.getFirstName(), person.getLastName(),
                                                person.getAddress(),
                                                person.getPhone()))
                                .toList();

                int numberOfAdults = (int) persons.stream().filter(person -> address.contains(person.getAddress())
                                && medicalRecords.stream()
                                                .filter(record -> record.getFirstName().equals(person.getFirstName())
                                                                && record.getLastName().equals(person.getLastName()))
                                                .findFirst()
                                                .map(record -> ageCalculationService
                                                                .calculateAge(record.getBirthdate()) > 18)
                                                .orElse(false))
                                .count();

                int numberOfChildren = filterPersonByStations.size() - numberOfAdults;

                log.info("Réponse retournée pour le numéro de caserne {} : {} adultes et {} enfants",
                                stationNumber, numberOfAdults, numberOfChildren);
                return new FirestationByPerson(filterPersonByStations, numberOfAdults, numberOfChildren);

        }
}
