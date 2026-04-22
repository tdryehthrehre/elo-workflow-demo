package de.erikschwob.elodemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sord_field")
public class SordField {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sord_id", nullable = false)
    private Sord sord;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "field_value", length = 4000)
    private String value;

    protected SordField() {}

    SordField(Sord sord, String fieldName, String value) {
        this.sord = sord;
        this.fieldName = fieldName;
        this.value = value;
    }

    public Long getId() { return id; }
    public String getFieldName() { return fieldName; }
    public String getValue() { return value; }
}
