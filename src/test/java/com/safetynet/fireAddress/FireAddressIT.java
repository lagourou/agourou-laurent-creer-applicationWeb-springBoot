package com.safetynet.fireAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.dto.FireAddress;
import com.safetynet.service.FireAddressService;
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
public class FireAddressIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FireAddressService fireAddressService;

    @Test
    void testRetourHabitantsValides() throws Exception {
        String address = "1509 Culver St";

        ResultActions response = mockMvc.perform(get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());

        List<FireAddress> fireAddresses = objectMapper.readValue(
                response.andReturn().getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FireAddress.class));

        assertThat(fireAddresses).isNotEmpty();
        assertThat(fireAddresses.get(0).name()).isNotEmpty();
        assertThat(fireAddresses.get(0).phone()).isNotEmpty();
    }

    @Test
    void testAucunHabitant() throws Exception {
        String address = "unknown address";

        mockMvc.perform(get("/fire")
                .param("address", address)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAdresseInvalide() throws Exception {
        String invalidAddress = "";

        mockMvc.perform(get("/fire")
                .param("address", invalidAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAdresseValide() throws Exception {
        String address = "1509 Culver St";

        List<FireAddress> fireAddresses = fireAddressService.getFireAddress(address);

        assertThat(fireAddresses).isNotEmpty();
        assertThat(fireAddresses.get(0).name()).isNotEmpty();
        assertThat(fireAddresses.get(0).medications()).isNotNull();
        assertThat(fireAddresses.get(0).allergies()).isNotNull();
        assertThat(fireAddresses.get(0).stationNumber()).isGreaterThan(0);
    }
}
