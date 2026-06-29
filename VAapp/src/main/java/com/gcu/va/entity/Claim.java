package com.gcu.va.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String claimType; // e.g. Disability, Education, Healthcare

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private LocalDate submittedDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "veteran_id")
    private Veteran veteran;

    // getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaimType() { return claimType; }
    public void setClaimType(String claimType) { this.claimType = claimType; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public LocalDate getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDate submittedDate) { this.submittedDate = submittedDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Veteran getVeteran() { return veteran; }
    public void setVeteran(Veteran veteran) { this.veteran = veteran; }
}
