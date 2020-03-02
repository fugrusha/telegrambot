package org.dental.backend.repository;

import org.dental.backend.domain.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends CrudRepository<Patient, UUID> {
}
