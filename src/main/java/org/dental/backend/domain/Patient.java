package org.dental.backend.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @OneToMany
    private List<Visit> visits;

    @OneToOne
    private AppUser appUser;
}
