package de.erikschwob.elodemo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.erikschwob.elodemo.model.NodeType;
import de.erikschwob.elodemo.model.Sord;
import de.erikschwob.elodemo.service.SordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SordController.class)
class SordControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean SordService sordService;

    private Sord sord(long id, String description) {
        Sord s = new Sord(description, null, NodeType.DOCUMENT);
        // reflect id via test helper — field is private, use a subclass trick is not needed,
        // SordResponse.from() only needs getShortDescription() and getFields(); id comes via getId()
        // so we create a minimal anonymous subclass that overrides getId()
        return new Sord(description, null, NodeType.DOCUMENT) {
            @Override public Long getId() { return id; }
        };
    }

    @Test
    @DisplayName("POST /api/sords creates a document and returns 201")
    void createSord_returns201() throws Exception {
        var request = new CreateSordRequest(
            "Invoice 2024-042", null, null,
            Map.of("amount", "1250.00", "vendor", "Acme Corp")
        );
        when(sordService.createDocument(eq("Invoice 2024-042"), isNull(), isNull(), any()))
            .thenReturn(sord(1L, "Invoice 2024-042"));

        mvc.perform(post("/api/sords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.shortDescription").value("Invoice 2024-042"));
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
        when(sordService.getById(1L)).thenReturn(sord(1L, "Contract A"));

        mvc.perform(get("/api/sords/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shortDescription").value("Contract A"));
    }

    @Test
    @DisplayName("GET /api/sords/{id} returns 404 for unknown ID")
    void getSord_unknownId_returns404() throws Exception {
        when(sordService.getById(99999L)).thenThrow(new NoSuchElementException("not found"));

        mvc.perform(get("/api/sords/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/sords/search returns matching documents")
    void searchSords_returnsMatches() throws Exception {
        when(sordService.search("invoice"))
            .thenReturn(List.of(sord(1L, "Invoice from Berlin")));

        mvc.perform(get("/api/sords/search").param("q", "invoice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].shortDescription").value("Invoice from Berlin"));
    }
}
