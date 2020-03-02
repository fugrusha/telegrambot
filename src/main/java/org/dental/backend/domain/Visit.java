package org.dental.backend.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Visit {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String complaint;

    private LocalDate desirableVisitDate;

    private VisitStatus visitStatus;

    private Instant createdAt;

    @ManyToOne
    private Patient patient;
}
