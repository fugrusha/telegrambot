package org.dental.backend.repository;

import org.dental.backend.domain.Visit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisitRepository extends CrudRepository<Visit, UUID> {
}
