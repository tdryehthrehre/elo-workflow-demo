package de.erikschwob.elodemo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.erikschwob.elodemo.service.SordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SordControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SordService sordService;

    @Test
    @DisplayName("POST /api/sords creates a document and returns 201")
    void createSord_returns201() throws Exception {
        var request = new CreateSordRequest(
            "Invoice 2024-042", null, null,
            Map.of("amount", "1250.00", "vendor", "Acme Corp")
        );

        mvc.perform(post("/api/sords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.shortDescription").value("Invoice 2024-042"))
            .andExpect(jsonPath("$.fields.amount").value("1250.00"));
    }

    @Test
    @DisplayName("POST /api/sords with empty description returns 400")
    void createSord_blankDescription_returns400() throws Exception {
        var request = new CreateSordRequest("", null, null, null);

        mvc.perform(post("/api/sords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/sords/{id} returns the document")
    void getSord_returnsDocument() throws Exception {
        var created = sordService.createDocument("Contract A", null, null, null);

        mvc.perform(get("/api/sords/" + created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shortDescription").value("Contract A"));
    }

    @Test
    @DisplayName("GET /api/sords/{id} returns 404 for unknown ID")
    void getSord_unknownId_returns404() throws Exception {
        mvc.perform(get("/api/sords/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/sords/search returns matching documents")
    void searchSords_returnsMatches() throws Exception {
        sordService.createDocument("Invoice from Berlin", null, null, null);
        sordService.createDocument("Contract Munich", null, null, null);

        mvc.perform(get("/api/sords/search").param("q", "invoice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].shortDescription").value("Invoice from Berlin"));
    }
}
