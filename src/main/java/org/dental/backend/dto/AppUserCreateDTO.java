package org.dental.backend.dto;

import lombok.Data;

@Data
public class AppUserCreateDTO {

    private Long chatId;

    private String firstName;

    private String lastName;

    private String username;
}
