package com.safetynet.dto;

import java.util.List;

public record FirestationByPerson(
        List<PersonByStation> persons,
        int numberOfAdults,
        int numberOfChildren) {
}
