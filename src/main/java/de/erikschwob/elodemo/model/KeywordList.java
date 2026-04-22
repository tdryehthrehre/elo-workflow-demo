package de.erikschwob.elodemo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "keyword_list")
public class KeywordList {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "keywordList", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<KeywordEntry> entries = new ArrayList<>();

    protected KeywordList() {}

    public KeywordList(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<KeywordEntry> getEntries() { return entries; }
}
