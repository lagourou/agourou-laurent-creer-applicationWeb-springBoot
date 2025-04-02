package com.safetynet.childAlert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.ChildrenByAddress;
import com.safetynet.service.ChildAlertService;

/**
 * Tests d'intégration pour le Endpoint ChildAlert.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class ChildAlertIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChildAlertService childAlertService;

    /**
     * Teste le fait que des enfants sont présents à l'adresse donnée.
     *
     * @throws Exception gère une erreur lors de l'exécution du test.
     */
    @Test
    void testChildrenFound() throws Exception {
        String address = "1509 Culver St";

        ResultActions response = mockMvc.perform(get("/childAlert")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        List<ChildrenByAddress> childrenByAddress = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ChildrenByAddress.class));

        assertThat(childrenByAddress).isNotEmpty();
        assertThat(childrenByAddress.get(0).firstName()).isNotEmpty();
        assertThat(childrenByAddress.get(0).lastName()).isNotEmpty();

    }

    /**
     * Teste le fait que aucun enfant n'est trouvé à l'adresse donnée.
     *
     * @throws Exception gère une erreur lors de l'exécution du test.
     */
    @Test
    void testNoChildrenFound() throws Exception {
        String address = "unknown address";

        mockMvc.perform(get("/childAlert")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Teste le fait qu'une adresse invalide est fournie.
     *
     * @throws Exception gère une erreur lors de l'exécution du test.
     */
    @Test
    void testInvalidAddress() throws Exception {
        String invalidAddress = "";

        mockMvc.perform(get("/childAlert")
                .param("address", invalidAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste le fait de récupérer des enfants à une adresse valide.
     *
     * @throws Exception gère une erreur lors de l'exécution du test.
     */
    @Test
    void testValidAddress() throws Exception {
        String address = "1509 Culver St";

        List<ChildrenByAddress> childrenByAddresses = childAlertService.getChildrenByAddress(address);

        assertThat(childrenByAddresses).isNotEmpty();
        assertThat(childrenByAddresses.get(0).firstName()).isNotEmpty();
        assertThat(childrenByAddresses.get(0).otherMembers()).isNotEmpty();
    }
}
