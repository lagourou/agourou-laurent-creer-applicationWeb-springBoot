package com.safetynet.childAlert;

import com.safetynet.controller.ChildAlertController;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.service.ChildAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChildAlertControllerTest {

    @Mock
    private ChildAlertService childAlertService;

    @InjectMocks
    private ChildAlertController childAlertController;

    @BeforeEach
    @SuppressWarnings(value = { "unused" })
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void testGetChildrenByAddress_ValidAddress_WithChildren() throws IOException {
        List<ChildrenByAddress> children = List.of(
                new ChildrenByAddress("John", "Doe", 10, List.of()),
                new ChildrenByAddress("Jane", "Doe", 8, List.of()));
        when(childAlertService.getChildrenByAddress("123 Main St")).thenReturn(children);

        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Le statut HTTP doit être 200");
        assertNotNull(response.getBody(), "Le corps de la réponse ne doit pas être null");
        assertFalse(response.getBody().isEmpty(), "La liste ne doit pas être vide");
        verify(childAlertService).getChildrenByAddress("123 Main St");
    }

    @Test
    void testGetChildrenByAddress_ValidAddress_NoChildren() throws IOException {
        when(childAlertService.getChildrenByAddress("123 Main St")).thenReturn(Collections.emptyList());

        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress("123 Main St");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Le statut HTTP doit être 204 (No Content)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verify(childAlertService).getChildrenByAddress("123 Main St");
    }

    @Test
    void testGetChildrenByAddress_InvalidAddress() throws IOException {
        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress("");

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(childAlertService); // Le service ne doit pas être appelé
    }

    @Test
    void testGetChildrenByAddress_NullAddress() throws IOException {
        ResponseEntity<List<ChildrenByAddress>> response = childAlertController.getChildrenByAddress(null);

        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Le statut HTTP doit être 400 (Bad Request)");
        assertNull(response.getBody(), "Le corps de la réponse doit être null");
        verifyNoInteractions(childAlertService);
    }

}
