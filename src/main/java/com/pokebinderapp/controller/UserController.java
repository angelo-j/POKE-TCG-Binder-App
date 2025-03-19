package com.pokebinderapp.controller;

import com.pokebinderapp.dao.UserDao;
import com.pokebinderapp.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@Tag(name = "User Management", description = "Endpoints for managing user accounts and balances.")
public class UserController {

    private final UserDao userDao;

    @Autowired
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @Operation(summary = "View all users",
            description = "Allows admins to retrieve detailed information about all users.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userDao.getUsers();
    }

    @Operation(summary = "Get a user by ID",
            description = "Allows authenticated users to retrieve all details about a specific user by ID.")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #userId == authentication.principal.userId)")
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        User user = userDao.getUserById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    @Operation(summary = "Get current user",
            description = "Allows authenticated users to retrieve details about the current user.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/me")
    public User getCurrentUser(Principal principal) {
        return userDao.getUserByUsername(principal.getName());
    }

    @Operation(summary = "Get user money",
            description = "Allows authenticated users to retrieve currency balance by user ID.")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #userId == authentication.principal.userId)")
    @GetMapping("/{userId}/money")
    public BigDecimal getUserMoney(@PathVariable int userId) {
        if (userDao.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return userDao.getMoney(userId);
    }

    @Operation(summary = "Update money",
            description = "Allows authenticated users to update their currency balance. Admins can modify any user's balance.")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #userId == authentication.principal.userId)")
    @PutMapping("/{userId}/money")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserMoney(@PathVariable int userId, @RequestBody BigDecimal newAmount) {
        if (userDao.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!userDao.updateMoney(userId, newAmount)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update money for user ID: " + userId);
        }
    }

    @Operation(summary = "Delete user",
            description = "Allows admins to delete any user.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int userId) {
        if (userDao.getUserById(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!userDao.deleteUser(userId)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete user ID: " + userId);
        }
    }
}
