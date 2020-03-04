package org.dental.backend.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Setter
@Getter
public class AppUser {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private Long chatId;

    private Integer stateId = 0;

    private String phone;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private Boolean isAdmin;

    private Boolean notified = false;

    @OneToOne
    private Patient patient;
}
