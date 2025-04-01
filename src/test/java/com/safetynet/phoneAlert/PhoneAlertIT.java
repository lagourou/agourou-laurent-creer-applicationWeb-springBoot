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
@DirtiesContext
public class PhoneAlertIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhoneAlertService phoneAlertService;

    @Test
    void testNumerosValides() throws Exception {
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

    @Test
    void testAucunNumero() throws Exception {
        int firestationNumber = 999;

        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(firestationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCaserneInvalide() throws Exception {
        int invalidFirestationNumber = -1;

        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(invalidFirestationNumber))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCaserneValide() throws Exception {
        int firestationNumber = 1;

        List<PhoneAlert> phoneAlerts = phoneAlertService.getPhoneAlert(firestationNumber);

        assertThat(phoneAlerts).isNotEmpty();
        assertThat(phoneAlerts.get(0).phone()).isNotEmpty();
    }
}
