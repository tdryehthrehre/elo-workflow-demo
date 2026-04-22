package de.erikschwob.elodemo.repository;

import de.erikschwob.elodemo.model.Mask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MaskRepository extends JpaRepository<Mask, Long> {
    Optional<Mask> findByName(String name);
}
