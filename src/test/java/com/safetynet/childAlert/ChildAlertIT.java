package com.safetynet.childAlert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.service.ChildAlertService;
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
@DirtiesContext // Réinitialise le contexte après chaque test
public class ChildAlertIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChildAlertService childAlertService;

    @Test
    void testGetChildrenByAddress_returnValidResponse() throws Exception {
        // Adresse existante avec enfants dans le fichier JSON
        String address = "1509 Culver St";

        // Exécution de la requête GET
        ResultActions response = mockMvc.perform(get("/childAlert")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON));

        // Vérification du statut HTTP et du contenu de la réponse
        response.andExpect(status().isOk());

        // Désérialisation et validation des résultats
        List<ChildrenByAddress> childrenByAddress = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ChildrenByAddress.class));

        assertThat(childrenByAddress).isNotEmpty();
        assertThat(childrenByAddress.get(0).firstName()).isNotEmpty();
        assertThat(childrenByAddress.get(0).lastName()).isNotEmpty();

    }

    @Test
    void testGetChildrenByAddress_returnNoContent() throws Exception {
        // Adresse existante mais sans enfants dans le fichier JSON
        String address = "unknown address";

        // Exécution de la requête GET
        mockMvc.perform(get("/childAlert")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Vérifie que le statut est 204
    }

    @Test
    void testGetChildrenByAddress_returnBadRequest() throws Exception {
        // Adresse invalide (vide ou null)
        String invalidAddress = "";

        // Exécution de la requête GET
        mockMvc.perform(get("/childAlert")
                .param("address", invalidAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Vérifie que le statut est 400
    }

    @Test
    void testGetChildrenByAddress_validateLogic() throws Exception {
        // Adresse existante dans le fichier JSON
        String address = "1509 Culver St";

        // Appel direct au service pour valider les résultats
        List<ChildrenByAddress> childrenByAddresses = childAlertService.getChildrenByAddress(address);

        // Vérifications
        assertThat(childrenByAddresses).isNotEmpty();
        assertThat(childrenByAddresses.get(0).firstName()).isNotEmpty(); // Vérifie que le prénom n'est pas vide
        assertThat(childrenByAddresses.get(0).otherMembers()).isNotEmpty(); // Vérifie les autres membres
    }
}
