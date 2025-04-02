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

/**
 * Tests d'intégration pour l'endpoint Firestation.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class FirestationIT {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Teste l'ajout d'une nouvelle caserne.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        public void testAddFirestation() throws Exception {
                Firestation firestation = new Firestation("1252 Roses St", 5);

                mockMvc.perform(post("/firestation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(firestation))))
                                .andExpect(status().isCreated());
        }

        /**
         * Teste la mise à jour d'une caserne qui existe.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        public void testUpdateFirestation() throws Exception {
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

        /**
         * Teste la suppression d'une caserne qui existe.
         *
         * @throws Exception gère l'erreur lors de l'exécution du test.
         */
        @Test
        public void testDeleteFirestation() throws Exception {
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
