package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.PhoneAlert;
import com.safetynet.model.Firestation;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

@Service
public class PhoneAlertService {
    private final DataLoad dataLoad;

    public PhoneAlertService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public List<PhoneAlert> getPhoneAlert(int firestationNumber) throws IOException {

        List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });
        List<Firestation> firestations = dataLoad.readJsonFile("firestations",
                new TypeReference<Map<String, List<Firestation>>>() {
                });

        List<Firestation> matchingStation = firestations.stream()
                .filter(firestation -> firestation.getStation() == firestationNumber).toList();

        List<PhoneAlert> filterPhoneAlert = persons.stream().filter(person -> matchingStation.stream()
                .anyMatch(firestation -> firestation.getAddress().equals(person.getAddress())))
                .map(person -> new PhoneAlert(person.getPhone())).toList();

        return filterPhoneAlert;
    }

}
