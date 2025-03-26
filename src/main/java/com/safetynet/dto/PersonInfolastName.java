package com.safetynet.dto;

import java.util.List;

public record PersonInfolastName(
        String lastName,
        String address,
        int age,
        String email,
        List<String> medications,
        List<String> allergies) {

}
