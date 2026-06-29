package com.gcu.va.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class RegistrationForm {

    // Login Information
    private String username;
    private String password;
    private String confirmPassword;

    // Veteran Information
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

	public String getDischargeStatus() {
		return dischargeStatus;
	}

	public void setDischargeStatus(String dischargeStatus) {
		this.dischargeStatus = dischargeStatus;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBranchOfService() {
		return branchOfService;
	}

	public void setBranchOfService(String branchOfService) {
		this.branchOfService = branchOfService;
	}

	public LocalDate getDateOfBirth() {
		// TODO Auto-generated method stub
		return dateOfBirth;
	}

	public LocalDate getServiceStartDate() {
		// TODO Auto-generated method stub
		return serviceStartDate;
	}

	public LocalDate getServiceEndDate() {
		// TODO Auto-generated method stub
		return serviceEndDate;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) {
	    this.dateOfBirth = dateOfBirth;
	}
	
	public void setServiceStartDate(LocalDate serviceStartDate) {
	    this.serviceStartDate = serviceStartDate;
	}
	
	public void setServiceEndDate(LocalDate serviceEndDate) {
	    this.serviceEndDate = serviceEndDate;
	}

    // Getters and Setters
}
