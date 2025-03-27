package com.safetynet.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.CommunityEmail;
import com.safetynet.model.Person;
import com.safetynet.service.dataService.DataLoad;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommunityEmailService {
    private final DataLoad dataLoad;

    public CommunityEmailService(DataLoad dataLoad) {
        this.dataLoad = dataLoad;
    }

    public List<CommunityEmail> getCommunityEmail(String city) throws IOException {
        List<Person> persons = dataLoad.readJsonFile("persons", new TypeReference<Map<String, List<Person>>>() {
        });

        List<CommunityEmail> emails = persons.stream().filter(person -> person.getCity().trim().equalsIgnoreCase(city))
                .map(person -> new CommunityEmail(person.getEmail())).toList();

        log.info("{} Emails trouvés pour la ville {}", emails.size(), city);
        return emails;
    }

}
