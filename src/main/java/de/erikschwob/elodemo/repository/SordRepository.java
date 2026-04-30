package de.erikschwob.elodemo.repository;

import de.erikschwob.elodemo.model.Sord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SordRepository extends JpaRepository<Sord, Long> {

    List<Sord> findByParentId(Long parentId);

    List<Sord> findByParentIsNull();

    @Query(value = """
            SELECT * FROM sord
            WHERE search_vector @@ plainto_tsquery('german', :term)
            ORDER BY ts_rank(search_vector, plainto_tsquery('german', :term)) DESC
            """, nativeQuery = true)
    List<Sord> searchByFullText(@Param("term") String term);

    // JPQL fallback — used by tests running against H2 (no tsvector support)
    @Query("SELECT s FROM Sord s WHERE LOWER(s.shortDescription) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Sord> searchByDescriptionLike(@Param("term") String term);
}
