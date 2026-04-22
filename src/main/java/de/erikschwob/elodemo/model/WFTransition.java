package de.erikschwob.elodemo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Immutable audit record of a single workflow status change. */
@Entity
@Table(name = "wf_transition")
public class WFTransition {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wf_node_id", nullable = false)
    private WFNode wfNode;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false, length = 30)
    private WFStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private WFStatus toStatus;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "wf_comment", length = 4000)
    private String comment;

    @Column(name = "transitioned_at", nullable = false, updatable = false)
    private LocalDateTime transitionedAt = LocalDateTime.now();

    protected WFTransition() {}

    WFTransition(WFNode node, WFStatus from, WFStatus to, String performedBy, String comment) {
        this.wfNode = node;
        this.fromStatus = from;
        this.toStatus = to;
        this.performedBy = performedBy;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public WFStatus getFromStatus() { return fromStatus; }
    public WFStatus getToStatus() { return toStatus; }
    public String getPerformedBy() { return performedBy; }
    public String getComment() { return comment; }
    public LocalDateTime getTransitionedAt() { return transitionedAt; }
}
