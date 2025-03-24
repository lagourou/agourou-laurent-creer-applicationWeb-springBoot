package com.safetynet.dto;

import java.util.List;

public record FireAddress(String name, String phone, int age, List<String> medications, List<String> allergies) {

}
