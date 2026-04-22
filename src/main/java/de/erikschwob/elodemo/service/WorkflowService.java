package de.erikschwob.elodemo.service;

import de.erikschwob.elodemo.model.*;
import de.erikschwob.elodemo.repository.SordRepository;
import de.erikschwob.elodemo.repository.WFNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class WorkflowService {

    private final WFNodeRepository wfNodeRepository;
    private final SordRepository sordRepository;

    public WorkflowService(WFNodeRepository wfNodeRepository, SordRepository sordRepository) {
        this.wfNodeRepository = wfNodeRepository;
        this.sordRepository = sordRepository;
    }

    /**
     * Starts a new workflow for a given Sord.
     * Each Sord can have at most one active workflow.
     */
    public WFNode startWorkflow(Long sordId, String assignee) {
        Sord sord = sordRepository.findById(sordId).orElseThrow(() ->
            new NoSuchElementException("Sord not found: " + sordId));

        if (wfNodeRepository.findBySordId(sordId).isPresent()) {
            throw new IllegalStateException("Workflow already exists for Sord: " + sordId);
        }

        WFNode node = new WFNode(sord, assignee);
        return wfNodeRepository.save(node);
    }

    /**
     * Performs a status transition on a workflow node.
     * The permitted transitions are enforced by WFStatus.canTransitionTo().
     */
    public WFNode transition(Long nodeId, WFStatus targetStatus,
                             String performedBy, String comment) {
        WFNode node = wfNodeRepository.findById(nodeId).orElseThrow(() ->
            new NoSuchElementException("WFNode not found: " + nodeId));

        node.transition(targetStatus, performedBy, comment);
        return wfNodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public WFNode getById(Long id) {
        return wfNodeRepository.findById(id).orElseThrow(() ->
            new NoSuchElementException("WFNode not found: " + id));
    }

    @Transactional(readOnly = true)
    public WFNode getBySordId(Long sordId) {
        return wfNodeRepository.findBySordId(sordId).orElseThrow(() ->
            new NoSuchElementException("No workflow for Sord: " + sordId));
    }

    @Transactional(readOnly = true)
    public List<WFNode> getByStatus(WFStatus status) {
        return wfNodeRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<WFNode> getByAssignee(String assignee) {
        return wfNodeRepository.findByAssignee(assignee);
    }
}
