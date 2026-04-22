package de.erikschwob.elodemo.service;

import de.erikschwob.elodemo.model.*;
import de.erikschwob.elodemo.repository.SordRepository;
import de.erikschwob.elodemo.repository.WFNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkflowServiceTest {

    @Autowired WorkflowService workflowService;
    @Autowired SordService sordService;
    @Autowired SordRepository sordRepository;
    @Autowired WFNodeRepository wfNodeRepository;

    private Sord testSord;

    @BeforeEach
    void setUp() {
        testSord = sordService.createDocument("Test Invoice 2024-001", null, null, null);
    }

    @Test
    @DisplayName("Starting a workflow sets status to INCOMING")
    void startWorkflow_setsStatusToIncoming() {
        WFNode node = workflowService.startWorkflow(testSord.getId(), "alice");

        assertThat(node.getId()).isNotNull();
        assertThat(node.getStatus()).isEqualTo(WFStatus.INCOMING);
        assertThat(node.getAssignee()).isEqualTo("alice");
        assertThat(node.getTransitions()).isEmpty();
    }

    @Test
    @DisplayName("Cannot start two workflows for the same Sord")
    void startWorkflow_duplicateThrows() {
        workflowService.startWorkflow(testSord.getId(), "alice");

        assertThatThrownBy(() -> workflowService.startWorkflow(testSord.getId(), "bob"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Workflow already exists");
    }

    @Test
    @DisplayName("Valid transition INCOMING -> REVIEW records audit entry")
    void transition_incomingToReview_succeeds() {
        WFNode node = workflowService.startWorkflow(testSord.getId(), "alice");

        WFNode updated = workflowService.transition(
            node.getId(), WFStatus.REVIEW, "alice", "Reviewed and forwarded");

        assertThat(updated.getStatus()).isEqualTo(WFStatus.REVIEW);
        assertThat(updated.getTransitions()).hasSize(1);
        assertThat(updated.getTransitions().get(0).getFromStatus()).isEqualTo(WFStatus.INCOMING);
        assertThat(updated.getTransitions().get(0).getToStatus()).isEqualTo(WFStatus.REVIEW);
        assertThat(updated.getTransitions().get(0).getPerformedBy()).isEqualTo("alice");
    }

    @Test
    @DisplayName("Full workflow lifecycle: INCOMING -> REVIEW -> APPROVAL -> ARCHIVE")
    void transition_fullLifecycle() {
        WFNode node = workflowService.startWorkflow(testSord.getId(), "alice");

        workflowService.transition(node.getId(), WFStatus.REVIEW, "alice", "OK");
        workflowService.transition(node.getId(), WFStatus.APPROVAL, "bob", "Approved");
        WFNode archived = workflowService.transition(node.getId(), WFStatus.ARCHIVE, "manager", "Filed");

        assertThat(archived.getStatus()).isEqualTo(WFStatus.ARCHIVE);
        assertThat(archived.getTransitions()).hasSize(3);
    }

    @Test
    @DisplayName("Invalid transition INCOMING -> ARCHIVE is rejected")
    void transition_invalidTransitionThrows() {
        WFNode node = workflowService.startWorkflow(testSord.getId(), "alice");

        assertThatThrownBy(() ->
            workflowService.transition(node.getId(), WFStatus.ARCHIVE, "alice", "Skip"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("not allowed");
    }

    @Test
    @DisplayName("ARCHIVE is a terminal state — no further transitions")
    void transition_fromArchiveThrows() {
        WFNode node = workflowService.startWorkflow(testSord.getId(), "alice");
        workflowService.transition(node.getId(), WFStatus.REVIEW, "alice", "");
        workflowService.transition(node.getId(), WFStatus.APPROVAL, "bob", "");
        workflowService.transition(node.getId(), WFStatus.ARCHIVE, "manager", "");

        assertThatThrownBy(() ->
            workflowService.transition(node.getId(), WFStatus.REVIEW, "alice", "Reopen"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Review can be sent back to INCOMING")
    void transition_reviewBackToIncoming() {
        WFNode node = workflowService.startWorkflow(testSord.getId(), "alice");
        workflowService.transition(node.getId(), WFStatus.REVIEW, "alice", "");

        WFNode returned = workflowService.transition(
            node.getId(), WFStatus.INCOMING, "reviewer", "Needs correction");

        assertThat(returned.getStatus()).isEqualTo(WFStatus.INCOMING);
        assertThat(returned.getTransitions()).hasSize(2);
    }

    @Test
    @DisplayName("Workflow not found throws NoSuchElementException")
    void getById_notFound() {
        assertThatThrownBy(() -> workflowService.getById(99999L))
            .isInstanceOf(NoSuchElementException.class);
    }
}
