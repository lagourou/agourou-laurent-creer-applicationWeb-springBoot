package com.safetynet.dto;

import java.util.List;

import com.safetynet.model.Person;

public record ChildrenByAddress(String firstName, String lastName, int age, List<Person> otherMembers) {
}
