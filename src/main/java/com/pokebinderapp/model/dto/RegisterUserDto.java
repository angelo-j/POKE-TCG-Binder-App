package com.pokebinderapp.model.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * RegisterUserDto is a class used to hold the registration information for a new user
 * that's sent from the client to the server for the register endpoint.
 *
 * The acronym DTO is being used for "data transfer object". It means that this type of
 * class is specifically created to transfer data between the client and the server.
 */
public class RegisterUserDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String confirmPassword;

    public RegisterUserDto() {
        // Default constructor needed for JSON deserialization
    }

    public RegisterUserDto(String username, String password, String confirmPassword, String role) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    // todo: Swagger registering as a field in dto
    public boolean isPasswordsMatch() {
        return password.equals(confirmPassword);
    }

}
