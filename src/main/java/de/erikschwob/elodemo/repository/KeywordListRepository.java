package de.erikschwob.elodemo.repository;

import de.erikschwob.elodemo.model.KeywordList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KeywordListRepository extends JpaRepository<KeywordList, Long> {
    Optional<KeywordList> findByName(String name);
}
