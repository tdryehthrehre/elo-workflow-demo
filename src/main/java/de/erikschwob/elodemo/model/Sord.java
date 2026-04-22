package de.erikschwob.elodemo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Core document object — analogous to the ELO Sord concept.
 * Represents both documents and folders (distinguished by NodeType).
 */
@Entity
@Table(name = "sord")
public class Sord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_description", nullable = false, length = 255)
    private String shortDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mask_id")
    private Mask mask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Sord parent;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, length = 20)
    private NodeType nodeType = NodeType.DOCUMENT;

    @OneToMany(mappedBy = "sord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SordField> fields = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected Sord() {}

    public Sord(String shortDescription, Mask mask, NodeType nodeType) {
        this.shortDescription = shortDescription;
        this.mask = mask;
        this.nodeType = nodeType;
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public void addField(String fieldName, String value) {
        fields.removeIf(f -> f.getFieldName().equals(fieldName));
        fields.add(new SordField(this, fieldName, value));
    }

    public Long getId() { return id; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public Mask getMask() { return mask; }
    public Sord getParent() { return parent; }
    public void setParent(Sord parent) { this.parent = parent; }
    public NodeType getNodeType() { return nodeType; }
    public List<SordField> getFields() { return fields; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
