package com.gcu.va.entity;

import jakarta.persistence.*;

//Import JPA annotations used to map this entity to the database.

/**
* Represents a user account in the VA Benefits and Eligibility Tracking System.
*
* This entity stores the user's login credentials and assigned role.
* It also maintains the relationship between a user account and its
* associated veteran record.
*/
@Entity
@Table(name = "users")
public class User {

 // Primary key generated automatically by the database.
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // Require a unique username that cannot be null in the database.
 @Column(unique = true, nullable = false)
 private String username;

 // Require a password value in the database.
 // Passwords should be stored securely as encoded values.
 @Column(nullable = false)
 private String password;

 // Store the user's role using its enum name.
 @Enumerated(EnumType.STRING)
 private Role role;

 // Associate this user account with its veteran record.
 // The relationship is managed by the "user" field in the Veteran entity.
 @OneToOne(mappedBy = "user")
 private Veteran veteran;

 // Getters and Setters

 /**
  * Retrieves the user's database identifier.
  *
  * @return the user ID
  */
 public Long getId() {
     // Return the user's ID.
     return id;
 }

 /**
  * Sets the user's database identifier.
  *
  * @param id the user ID to assign
  */
 public void setId(Long id) {
     // Assign the supplied user ID.
     this.id = id;
 }

 /**
  * Retrieves the username associated with the account.
  *
  * @return the username
  */
 public String getUsername() {
     // Return the stored username.
     return username;
 }

 /**
  * Sets the username associated with the account.
  *
  * @param username the username to store
  */
 public void setUsername(String username) {
     // Assign the supplied username.
     this.username = username;
 }

 /**
  * Retrieves the account password value.
  *
  * @return the stored password value
  */
 public String getPassword() {
     // Return the stored password value.
     return password;
 }

 /**
  * Sets the account password value.
  *
  * @param password the password value to store
  */
 public void setPassword(String password) {
     // Assign the supplied password value.
     this.password = password;
 }

 /**
  * Retrieves the role assigned to the user.
  *
  * @return the user's role
  */
 public Role getRole() {
     // Return the assigned role.
     return role;
 }

 /**
  * Sets the role assigned to the user.
  *
  * @param role the role to assign
  */
 public void setRole(Role role) {
     // Assign the supplied role.
     this.role = role;
 }

 /**
  * Associates the user account with a veteran record.
  *
  * @param veteran the veteran record associated with the account
  */
 public void setVeteran(Veteran veteran) {
     // Store the associated veteran record.
     this.veteran = veteran;
 }

 /**
  * Retrieves the veteran record associated with the user account.
  *
  * @return the associated veteran record, or null if none is assigned
  */
 public Veteran getVeteran() {
     // Return the associated veteran record.
     return veteran;
 }
}