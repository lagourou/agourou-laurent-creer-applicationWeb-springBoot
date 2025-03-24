package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.model.MedicalRecord;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

@Service
public class ChildAlertService {
    private final AgeCalculationService ageCalculationService;
    private final DataLoad dataLoad;

    public ChildAlertService(AgeCalculationService ageCalculationService, DataLoad dataLoad) {
        this.ageCalculationService = ageCalculationService;
        this.dataLoad = dataLoad;
    }

    public int getAge(String birthdate) {
        return ageCalculationService.calculateAge(birthdate);
    }

    public List<ChildrenByAddress> getchildrenByAddress(String address) throws IOException {

        String childAddress = address.trim();
        List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });
        List<MedicalRecord> medicalRecords = dataLoad.readJsonFile("medicalrecords",
                new TypeReference<Map<String, List<MedicalRecord>>>() {
                });

        List<ChildrenByAddress> filterChildrenByAddress = persons.stream()
                .filter(person -> childAddress.equalsIgnoreCase(person.getAddress()))
                .filter(person -> {
                    MedicalRecord record = medicalRecords.stream()
                            .filter(medical -> medical.getFirstName().equals(person.getFirstName())
                                    && medical.getLastName().equals(person.getLastName()))
                            .findFirst()
                            .orElse(null);
                    if (record != null) {
                        int age = getAge(record.getBirthdate());
                        return age <= 18;
                    }
                    return false;
                })
                .map(person -> {
                    List<Person> otherMembers = persons.stream()
                            .filter(member -> childAddress.equalsIgnoreCase(member.getAddress()))
                            .filter(member -> !member.getFirstName().equals(person.getFirstName())
                                    && !member.getLastName().equals(person.getLastName()))
                            .toList();

                    MedicalRecord record = medicalRecords.stream()
                            .filter(medical -> medical.getFirstName().equals(person.getFirstName())
                                    && medical.getLastName().equals(person.getLastName()))
                            .findFirst()
                            .orElse(null);

                    int age = 0;
                    if (record != null) {
                        age = getAge(record.getBirthdate());
                    }

                    return new ChildrenByAddress(person.getFirstName(), person.getLastName(), age, otherMembers);
                })
                .toList();

        if (filterChildrenByAddress.isEmpty()) {
            return List.of();
        }
        return filterChildrenByAddress;
    }

}
