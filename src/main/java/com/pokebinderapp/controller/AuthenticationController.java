package com.pokebinderapp.controller;

import com.pokebinderapp.model.dto.LoginDto;
import com.pokebinderapp.model.dto.LoginResponseDto;
import com.pokebinderapp.model.dto.RegisterUserDto;
import com.pokebinderapp.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.pokebinderapp.exception.DaoException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pokebinderapp.dao.UserDao;
import com.pokebinderapp.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

/**
 * AuthenticationController is a class used for handling requests to authenticate Users.
 *
 * It depends on an instance of a UserDao for retrieving and storing user data. This is provided
 * through dependency injection.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Tag(name = "Authentication Management", description = "Endpoints for managing user accounts.")
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @Operation(summary = "Login",
            description = "Allows users to access their account.")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponseDto login(@Valid @RequestBody LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            if(authentication.isAuthenticated()){
                String jwt = tokenProvider.createToken(authentication, false);
                User user = userDao.getUserByUsername(loginDto.getUsername());
                return new LoginResponseDto(jwt, user);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage());
        }
    }

    @Operation(summary = "Register user",
            description = "Allows creation of new user accounts.")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public User register(@Valid @RequestBody RegisterUserDto newUser) {

        if (!newUser.isPasswordsMatch()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password and confirm password do not match");
        }

        try {
            if (userDao.getUserByUsername(newUser.getUsername()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists.");
            }

            // Force role to "ROLE_USER" regardless of what is in newUser
            User user = new User(newUser.getUsername(), newUser.getPassword(), "ROLE_USER");

            user = userDao.createUser(user);

            // Fetch user with money after creation
            user = userDao.getUserById(user.getId());

            return user;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage());
        }
    }


}
