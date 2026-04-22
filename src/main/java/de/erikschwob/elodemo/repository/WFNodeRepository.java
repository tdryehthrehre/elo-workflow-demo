package de.erikschwob.elodemo.repository;

import de.erikschwob.elodemo.model.WFNode;
import de.erikschwob.elodemo.model.WFStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WFNodeRepository extends JpaRepository<WFNode, Long> {

    Optional<WFNode> findBySordId(Long sordId);

    List<WFNode> findByStatus(WFStatus status);

    List<WFNode> findByAssignee(String assignee);
}
