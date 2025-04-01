package com.safetynet.communityEmail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safetynet.dto.CommunityEmail;
import com.safetynet.model.Person;
import com.safetynet.service.CommunityEmailService;
import com.safetynet.service.dataService.DataLoad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommunityEmailServiceTest {

    @Mock
    private DataLoad dataLoad;

    @InjectMocks
    private CommunityEmailService communityEmailService;

    private List<Person> persons;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);

        persons = List.of(
                new Person("John", "Doe", "123 Main St", "City1", "12345", "123-456-7890", "john.doe@email.com"),
                new Person("Jane", "Doe", "456 Oak St", "City1", "12345", "987-654-3210", "jane.doe@email.com"),
                new Person("Bob", "Smith", "789 Pine St", "City2", "54321", "456-789-0123", "bob.smith@email.com"));
    }

    @Test
    void testGetCommunityEmail_ValidCity() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);

        List<CommunityEmail> emails = communityEmailService.getCommunityEmail("City1");

        assertNotNull(emails, "La liste des emails ne doit pas être null");
        assertEquals(2, emails.size(), "La liste doit contenir 2 emails");
        assertTrue(emails.stream().anyMatch(email -> "john.doe@email.com".equals(email.email())),
                "Email de John doit être présent");
        assertTrue(emails.stream().anyMatch(email -> "jane.doe@email.com".equals(email.email())),
                "Email de Jane doit être présent");
        verify(dataLoad).readJsonFile(eq("persons"), any(TypeReference.class));
    }

    @Test
    void testGetCommunityEmail_InvalidCity() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);

        List<CommunityEmail> emails = communityEmailService.getCommunityEmail("InvalidCity");

        assertNotNull(emails, "La liste des emails ne doit pas être null");
        assertTrue(emails.isEmpty(), "La liste doit être vide pour une ville invalide");
        verify(dataLoad).readJsonFile(eq("persons"), any(TypeReference.class));
    }

    @Test
    void testGetCommunityEmail_EmptyCity() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);

        List<CommunityEmail> emails = communityEmailService.getCommunityEmail("");

        assertNotNull(emails, "La liste des emails ne doit pas être null");
        assertTrue(emails.isEmpty(), "La liste doit être vide pour une ville vide");
        verify(dataLoad).readJsonFile(eq("persons"), any(TypeReference.class));
    }

    @Test
    void testGetCommunityEmail_NullCity() throws IOException {
        when(dataLoad.readJsonFile(eq("persons"), any(TypeReference.class))).thenReturn(persons);

        List<CommunityEmail> emails = communityEmailService.getCommunityEmail(null);

        assertNotNull(emails, "La liste des emails ne doit pas être null");
        assertTrue(emails.isEmpty(), "La liste doit être vide pour une ville null");
        verify(dataLoad).readJsonFile(eq("persons"), any(TypeReference.class));
    }
}
