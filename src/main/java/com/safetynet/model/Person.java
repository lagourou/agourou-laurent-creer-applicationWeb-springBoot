package com.safetynet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore les champs inconnus
public class Person {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;

    public Person() {
    }

    public Person(String firstName, String lastName, String address, String city, String zip, String phone,
            String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

}