package com.pokebinderapp.controller;

import com.pokebinderapp.dao.BinderDao;
import com.pokebinderapp.dao.CardDao;
import com.pokebinderapp.dao.UserDao;
import com.pokebinderapp.model.Binder;
import com.pokebinderapp.model.Card;
import com.pokebinderapp.model.User;
import com.pokebinderapp.model.dto.BinderCardDto;
import com.pokebinderapp.model.dto.RenameBinderDto;
import com.pokebinderapp.model.dto.BuyCardRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/binders")
@Tag(name = "Binder Management", description = "Endpoints for managing binders and their cards.")
public class BinderController {

    private static final Logger logger = Logger.getLogger(BinderController.class.getName());

    private final BinderDao binderDao;
    private final CardDao cardDao;
    private final UserDao userDao;

    @Autowired
    public BinderController(BinderDao binderDao, CardDao cardDao, UserDao userDao) {
        this.binderDao = binderDao;
        this.cardDao = cardDao;
        this.userDao = userDao;
    }

    @Operation(summary = "Create a binder",
            description = "Allows authenticated users can create binders to manage their collections.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Binder> createBinder(@RequestBody Binder binder, Principal principal) {
        try {
            Binder newBinder = binderDao.createBinder(binder);
            return new ResponseEntity<>(newBinder, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating binder", e);
        }
    }

    @Operation(summary = "Create a binder for a user",
            description = "Allows admins to create a new binder for any user.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/user/{username}/binders")
    public ResponseEntity<Binder> createBinderForUser(@PathVariable String username, @RequestBody Binder binder) {
        try {
            User user = userDao.getUserByUsername(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            binder.setUserId(user.getId());
            Binder newBinder = binderDao.createBinder(binder);
            return new ResponseEntity<>(newBinder, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating binder for user", e);
        }
    }

    @Operation(summary = "Simple binder list",
            description = "Allows admins to retrieve information about all binders in the database, omitting cards.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<Binder> getAllBinders() {
        try {
            List<Binder> binders = binderDao.getAllBinders();

            // Ensure that admins don't see card details in this view
            for (Binder binder : binders) {
                binder.setCards(new ArrayList<>()); // Clear the card list
            }

            return binders;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving binders", e);
        }
    }


    @Operation(summary = "Get my binders",
            description = "Allows authenticated users to see a list of current user's binders and the cards they contain.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/mine")
    public List<Binder> getMyBinders(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        User user = userDao.getUserByUsername(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return binderDao.getBindersByUserId(user.getId());
    }

    @Operation(summary = "Get a binder by ID",
            description = "Allows authenticated users to retrieve detailed information about a specific binder by its unique ID.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{binderId}")
    public Binder getBinderById(@PathVariable int binderId, Principal principal) {
        try {
            Binder binder = binderDao.getBinderById(binderId);
            if (binder == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Binder not found");
            }
            // Check if the authenticated user owns the binder or is admin.
            if (!isAuthorizedUser(principal, binder.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            return binder;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving binder", e);
        }
    }

    @Operation(summary = "Get binders by user ID",
            description = "Allows authenticated users to retrieve detailed information about binders associated with a user ID.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public List<Binder> getBindersByUserId(@PathVariable int userId, Principal principal) {
        try {
            if (!isAuthorizedUser(principal, userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            return binderDao.getBindersByUserId(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving binders for user", e);
        }
    }

    @Operation(summary = "Get cards in a binder",
            description = "Allows authenticated users to retrieve only the cards in a binder.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{binderId}/cards")
    public List<Card> getCardsInBinder(@PathVariable int binderId, Principal principal) {
        try {
            Binder binder = binderDao.getBinderById(binderId);
            if (binder == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Binder not found");
            }
            if (!isAuthorizedUser(principal, binder.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            return cardDao.getCardsInBinder(binderId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving cards in binder", e);
        }
    }

    @Operation(summary = "Rename binder",
            description = "Allows admins to rename any binder. (For correcting inappropriate binder names.)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{binderId}")
    public ResponseEntity<Void> renameBinder(@PathVariable int binderId, @RequestBody RenameBinderDto renameBinderDto) {
        try {
            boolean success = binderDao.updateBinderName(binderId, renameBinderDto.getName());
            if (!success) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update binder name");
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error renaming binder", e);
        }
    }

    @Operation(summary = "Delete binder",
            description = "Allows authenticated users to delete a binder by binder ID.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{binderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBinder(@PathVariable int binderId, Principal principal) {
        try {
            Binder binder = binderDao.getBinderById(binderId);
            if (binder == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Binder not found");
            }
            if (!isAuthorizedUser(principal, binder.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            boolean success = binderDao.deleteBinder(binderId);
            if (!success) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete binder");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting binder", e);
        }
    }


    @Operation(summary = "Add a card to a binder",
            description = "Allows authenticated users to add a card to a binder, no currency exchange.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/{binderId}/cards")
    public ResponseEntity<Void> addCardToBinder(@PathVariable int binderId, @RequestBody BinderCardDto binderCardDto, Principal principal) {
        try {
            Binder binder = binderDao.getBinderById(binderId);
            if (binder == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Binder not found");
            }
            if (!isAuthorizedUser(principal, binder.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            // The user only provides the cardId. The DAO will call the Pokemon API
            // to retrieve full card details and map to a BinderCardDto.
            Card card = new Card();
            card.setCardId(binderCardDto.getCardId());
            boolean success = cardDao.addCardToBinder(binderId, card);
            if (!success) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add card to binder");
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding card to binder", e);
        }
    }

    @Operation(summary = "Buy a card",
            description = "Allows authenticated users to buy a card for a binder using in-app currency.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/buyCard")
    public ResponseEntity<Void> buyCardToBinder(@RequestBody BuyCardRequestDto buyRequest, Principal principal) {
        try {
            boolean success = cardDao.buyCardToBinder(
                    buyRequest.getBinderId(),
                    buyRequest.getCardId(),
                    buyRequest.getPrice(),
                    buyRequest.getUserId(),
                    buyRequest.getPreferredPriceKey()
            );
            if (success) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to buy card to binder");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error buying card to binder", e);
        }
    }

    @Operation(summary = "Remove a card from a binder",
            description = "Authenticated users can remove a card from a binder, no currency exchange.")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{binderId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCardFromBinder(@PathVariable int binderId, @PathVariable String cardId, Principal principal) {
        try {
            Binder binder = binderDao.getBinderById(binderId);
            if (binder == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Binder not found");
            }
            if (!isAuthorizedUser(principal, binder.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            boolean success = cardDao.removeCardFromBinder(binderId, cardId);
            if (!success) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove card from binder");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing card from binder", e);
        }
    }

    @Operation(summary = "Sell a card from a binder",
            description = "Allows authenticated users to sell a card at market rate from a binder.")
    @PostMapping("/{binderId}/sell/{cardId}")
    public ResponseEntity<Void> sellCardFromBinder(@PathVariable int binderId,
                                                   @PathVariable String cardId,
                                                   Principal principal) {
        try {
            Binder binder = binderDao.getBinderById(binderId);
            if (binder == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Binder not found");
            }
            if (!isAuthorizedUser(principal, binder.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }
            boolean success = cardDao.sellCardFromBinder(binderId, cardId, null, binder.getUserId());
            if (success) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to sell card from binder");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error selling card from binder", e);
        }
    }

    // Utility method for checking if the authenticated user is allowed to access a binder.
    private boolean isAuthorizedUser(Principal principal, int binderOwnerId) {
        User user = userDao.getUserByUsername(principal.getName());

        // Check if user is admin
        if (user != null && user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return true;
        }

        // Otherwise, check if they own the binder
        return user != null && user.getId() == binderOwnerId;
    }

}
