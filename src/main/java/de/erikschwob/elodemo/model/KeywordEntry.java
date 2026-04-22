package de.erikschwob.elodemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "keyword_entry")
public class KeywordEntry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_list_id", nullable = false)
    private KeywordList keywordList;

    @Column(name = "entry_value", nullable = false, length = 200)
    private String value;

    @Column(name = "sort_order")
    private int sortOrder;

    protected KeywordEntry() {}

    public KeywordEntry(KeywordList list, String value, int sortOrder) {
        this.keywordList = list;
        this.value = value;
        this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public String getValue() { return value; }
    public int getSortOrder() { return sortOrder; }
}
