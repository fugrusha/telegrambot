package org.dental.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AppUserReadDTO {

    private UUID id;

    private Long chatId;

    private Integer stateId;

    private String phone;

    private String firstName;

    private String lastName;

    private String email;

    private Boolean isAdmin;

    private Boolean notified;

    private PatientReadDTO patient;
}
