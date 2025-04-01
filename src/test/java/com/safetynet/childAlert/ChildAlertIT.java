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
@DirtiesContext
public class ChildAlertIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChildAlertService childAlertService;

    @Test
    void testRetourneEnfantsValide() throws Exception {
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

    @Test
    void testRetourneAucunEnfant() throws Exception {
        String address = "unknown address";

        mockMvc.perform(get("/childAlert")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAdresseInvalide() throws Exception {
        String invalidAddress = "";

        mockMvc.perform(get("/childAlert")
                .param("address", invalidAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAdresseValide() throws Exception {
        String address = "1509 Culver St";

        List<ChildrenByAddress> childrenByAddresses = childAlertService.getChildrenByAddress(address);

        assertThat(childrenByAddresses).isNotEmpty();
        assertThat(childrenByAddresses.get(0).firstName()).isNotEmpty();
        assertThat(childrenByAddresses.get(0).otherMembers()).isNotEmpty();
    }
}
