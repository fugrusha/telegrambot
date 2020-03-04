package org.dental.backend.dto;

import lombok.Data;

@Data
public class AppUserPatchDTO {

    private Integer stateId;

    private String phone;

    private String email;
}
