package com.safetynet.phoneAlert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.PhoneAlert;
import com.safetynet.service.PhoneAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'endpoint PhoneAlert.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class PhoneAlertIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhoneAlertService phoneAlertService;

    /**
     * Teste la récupération des numéros de téléphone pour une caserne valide.
     * Vérifie que le statut HTTP est 200 et que les numéros de téléphone sont
     * retournés correctement.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testValidsNumbers() throws Exception {
        int firestationNumber = 1;

        ResultActions response = mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(firestationNumber))
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        List<PhoneAlert> phoneAlerts = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PhoneAlert.class));

        assertThat(phoneAlerts).isNotEmpty();
        assertThat(phoneAlerts.get(0).phone()).isNotEmpty();
    }

    /**
     * Teste la récupération des numéros de téléphone pour une caserne valide mais
     * sans résultats.
     * Vérifie que le statut HTTP est 204.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testNoNumber() throws Exception {
        int firestationNumber = 999;

        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(firestationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Teste la récupération des numéros de téléphone pour une caserne invalide.
     * Vérifie que le statut HTTP est 400.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testInvalidFirestation() throws Exception {
        int invalidFirestationNumber = -1;

        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(invalidFirestationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la récupération des numéros de téléphone pour une caserne valide.
     * Vérifie que les numéros de téléphone retournés sont corrects.
     *
     * @throws Exception s'il y a une erreur lors de l'exécution de la requête.
     */
    @Test
    void testValidFirestation() throws Exception {
        int firestationNumber = 1;

        List<PhoneAlert> phoneAlerts = phoneAlertService.getPhoneAlert(firestationNumber);

        assertThat(phoneAlerts).isNotEmpty();
        assertThat(phoneAlerts.get(0).phone()).isNotEmpty();
    }
}
