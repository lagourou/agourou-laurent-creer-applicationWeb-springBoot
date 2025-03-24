package com.safetynet.dto;

import java.util.List;
import java.util.Map;

public record ChildrenByAddress(String firstName, String lastName, int age, List<Map<String, String>> otherMembers) {
}
