package com.pokebinderapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Model class representing an application user.
 *
 * Contains information about the user - their id, username, address information,
 * password (hashed) and authorities (user roles).
 */
public class User {

   private int id;
   private String username;
   @JsonIgnore
   private String hashedPassword;
   private String role;

   /**
    Money lets the player buy and sell cards!
    */
   private BigDecimal money;

   public User() {
      // Starting money.
      this.money = BigDecimal.valueOf(100.00);
   }

   public User(int id, String username, String hashedPassword, String role) {
      this.id = id;
      this.username = username;
      this.hashedPassword = hashedPassword;
      this.role = role;
   }

   public User(String username, String hashedPassword, String role) {
      this(0, username, hashedPassword, role);
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getHashedPassword() {
      return hashedPassword;
   }

   public void setHashedPassword(String hashedPassword) {
      this.hashedPassword = hashedPassword;
   }

   public String getRole() {
      return role;
   }

   public BigDecimal getMoney() {return money;}

   public void setMoney(BigDecimal money){this.money = money;}

   public void setRole(String role) {
      this.role = role != null && !role.startsWith("ROLE_")
              ? "ROLE_" + role.toUpperCase()
              : role;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id == user.id &&
              Objects.equals(username, user.username) &&
              Objects.equals(hashedPassword, user.hashedPassword) &&
              Objects.equals(role, user.role);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, hashedPassword, role);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", hashedPassword='" + hashedPassword + '\'' +
              ", role='" + role + '\'' +
              '}';
   }

}
