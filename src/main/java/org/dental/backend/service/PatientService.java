package org.dental.backend.service;

import org.dental.backend.dto.PatientReadDTO;
import org.dental.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public PatientReadDTO createPatient() {
        return null;
    }

    public PatientReadDTO getPatient() {
        return null;
    }
}
