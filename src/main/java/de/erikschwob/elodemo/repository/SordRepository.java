package de.erikschwob.elodemo.repository;

import de.erikschwob.elodemo.model.Sord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SordRepository extends JpaRepository<Sord, Long> {

    List<Sord> findByParentId(Long parentId);

    List<Sord> findByParentIsNull();

    @Query("SELECT s FROM Sord s WHERE LOWER(s.shortDescription) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Sord> searchByDescription(@Param("term") String term);
}
