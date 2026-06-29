package com.gcu.va.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "veteran")
public class Veteran {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@OneToOne
@JoinColumn(name = "user_id")
private User user;

@OneToMany(mappedBy = "veteran", cascade = CascadeType.ALL)
private java.util.List<Claim> claims;

private String firstName;
private String lastName;
private String email;



@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate dateOfBirth;

private String branchOfService;

@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate serviceStartDate;

@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate serviceEndDate;

private String dischargeStatus;

private String eligibilityStatus;

private String eligibilityReason;

// ===== GETTERS & SETTERS =====

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getFirstName() { return firstName; }
public void setFirstName(String firstName) { this.firstName = firstName; }

public String getLastName() { return lastName; }
public void setLastName(String lastName) { this.lastName = lastName; }

public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }

public LocalDate getDateOfBirth() { return dateOfBirth; }
public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

public String getBranchOfService() { return branchOfService; }
public void setBranchOfService(String branchOfService) { this.branchOfService = branchOfService; }

public LocalDate getServiceStartDate() { return serviceStartDate; }
public void setServiceStartDate(LocalDate serviceStartDate) { this.serviceStartDate = serviceStartDate; }

public LocalDate getServiceEndDate() { return serviceEndDate; }
public void setServiceEndDate(LocalDate serviceEndDate) { this.serviceEndDate = serviceEndDate; }

public String getDischargeStatus() { return dischargeStatus; }
public void setDischargeStatus(String dischargeStatus) { this.dischargeStatus = dischargeStatus; }

public void setUser(User user) {
	this.user = user;
	
}
public String getEligibilityStatus() {
    return eligibilityStatus;
}

public void setEligibilityStatus(String eligibilityStatus) {
    this.eligibilityStatus = eligibilityStatus;
}

public String getEligibilityReason() {
    return eligibilityReason;
}

public void setEligibilityReason(String eligibilityReason) {
    this.eligibilityReason = eligibilityReason;
}

public java.util.List<Claim> getClaims() {
    return claims;
}

public void setClaims(java.util.List<Claim> claims) {
    this.claims = claims;
}


}
