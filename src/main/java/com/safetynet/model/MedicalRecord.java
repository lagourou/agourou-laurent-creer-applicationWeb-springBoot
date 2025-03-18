package com.safetynet.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class MedicalRecord {
    private String firstName;
    private String lastName;
    private LocalDate birthdDate;
    private List<String> medications;
    private List<String> allergies;

    public MedicalRecord() {
    }

    public MedicalRecord(String firstName, String lastName, LocalDate birthdDate, List<String> medications,
            List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdDate = birthdDate;
        this.medications = medications;
        this.allergies = allergies;
    }

}
