package org.dental.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PatientReadDTO {

    private UUID id;

    private AppUserReadDTO appUser;
}
