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

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // Réinitialise le contexte entre chaque test
public class PhoneAlertIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhoneAlertService phoneAlertService;

    @Test
    void testGetPhoneAlert_returnValidResponse() throws Exception {
        // Numéro de caserne existant dans le fichier JSON
        int firestationNumber = 1;

        // Exécution de la requête GET
        ResultActions response = mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(firestationNumber))
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        List<PhoneAlert> phoneAlerts = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, PhoneAlert.class));

        assertThat(phoneAlerts).isNotEmpty();
        assertThat(phoneAlerts.get(0).phone()).isNotEmpty(); // Vérifie qu'un numéro de téléphone est présent
    }

    @Test
    void testGetPhoneAlert_returnNoContent() throws Exception {
        // Numéro de caserne inexistant dans le fichier JSON
        int firestationNumber = 999;

        // Exécution de la requête GET
        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(firestationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204
    }

    @Test
    void testGetPhoneAlert_returnBadRequest() throws Exception {
        // Paramètre "firestation" invalide (zéro ou négatif)
        int invalidFirestationNumber = -1;

        // Exécution de la requête GET
        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(invalidFirestationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400
    }

    @Test
    void testGetPhoneAlert_validateLogic() throws Exception {
        // Numéro de caserne existant dans le fichier JSON
        int firestationNumber = 1;

        // Appel direct au service pour valider les résultats
        List<PhoneAlert> phoneAlerts = phoneAlertService.getPhoneAlert(firestationNumber);

        // Vérifications
        assertThat(phoneAlerts).isNotEmpty();
        assertThat(phoneAlerts.get(0).phone()).isNotEmpty(); // Vérifie qu'un numéro de téléphone est présent
    }
}
