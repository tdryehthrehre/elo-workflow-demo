package de.erikschwob.elodemo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Workflow instance tied to a Sord.
 * Models the concept of a workflow node in ELO — tracking status,
 * assignee, and the complete transition history.
 */
@Entity
@Table(name = "wf_node")
public class WFNode {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sord_id", nullable = false)
    private Sord sord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WFStatus status = WFStatus.INCOMING;

    @Column(length = 100)
    private String assignee;

    @Column(name = "wf_comment", length = 4000)
    private String comment;

    @OneToMany(mappedBy = "wfNode", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("transitionedAt ASC")
    private List<WFTransition> transitions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected WFNode() {}

    public WFNode(Sord sord, String assignee) {
        this.sord = sord;
        this.assignee = assignee;
    }

    /**
     * Transitions to a new status and records the change.
     * Throws IllegalStateException if the transition is not permitted.
     */
    public WFTransition transition(WFStatus target, String performedBy, String comment) {
        if (!this.status.canTransitionTo(target)) {
            throw new IllegalStateException(
                String.format("Transition from %s to %s is not allowed", this.status, target));
        }
        WFTransition t = new WFTransition(this, this.status, target, performedBy, comment);
        this.transitions.add(t);
        this.status = target;
        this.updatedAt = LocalDateTime.now();
        return t;
    }

    public Long getId() { return id; }
    public Sord getSord() { return sord; }
    public WFStatus getStatus() { return status; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public List<WFTransition> getTransitions() { return transitions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
