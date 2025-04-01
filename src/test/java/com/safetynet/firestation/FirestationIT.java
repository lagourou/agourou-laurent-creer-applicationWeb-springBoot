package com.safetynet.firestation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.model.Firestation;

@SpringBootTest
@AutoConfigureMockMvc
public class FirestationIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void testAjoutFirestation() throws Exception {
                Firestation firestation = new Firestation("1252 Roses St", 5);

                mockMvc.perform(post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(firestation))))
                                .andExpect(status().isCreated());

        }

        @Test
        public void testMiseAJourFirestation() throws Exception {
                Firestation firestationExisting = new Firestation("1252 Roses St", 5);
                mockMvc.perform(post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(firestationExisting))))
                                .andExpect(status().isCreated());

                Firestation updateFirestation = new Firestation("1252 Roses St", 3);
                mockMvc.perform(put("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(updateFirestation))))
                                .andExpect(status().isOk());
        }

        @Test
        public void testSuppressionFirestation() throws Exception {
                Firestation firestation = new Firestation("1252 Roses St", 3);
                mockMvc.perform(post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(firestation))))
                                .andExpect(status().isCreated());

                mockMvc.perform(delete("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(firestation))))
                                .andExpect(status().isOk());
        }
}
