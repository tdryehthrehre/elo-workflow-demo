package de.erikschwob.elodemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mask_field")
public class MaskField {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mask_id", nullable = false)
    private Mask mask;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "field_type", nullable = false, length = 50)
    private String fieldType;

    @Column(nullable = false)
    private boolean required;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_list_id")
    private KeywordList keywordList;

    @Column(name = "sort_order")
    private int sortOrder;

    protected MaskField() {}

    public MaskField(Mask mask, String fieldName, String fieldType, boolean required, int sortOrder) {
        this.mask = mask;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.required = required;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public String getFieldName() { return fieldName; }
    public String getFieldType() { return fieldType; }
    public boolean isRequired() { return required; }
    public KeywordList getKeywordList() { return keywordList; }
    public int getSortOrder() { return sortOrder; }
    public void setKeywordList(KeywordList kwList) { this.keywordList = kwList; }
}
