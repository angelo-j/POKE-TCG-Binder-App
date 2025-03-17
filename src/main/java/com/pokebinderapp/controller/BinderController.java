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

    // Create Binder (Authenticated user or Admin)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Binder> createBinder(@RequestBody Binder binder, Principal principal) {
        try {
            // Optionally check that binder.getUserId() matches the authenticated user's id,
            // unless an admin is creating the binder.
            Binder newBinder = binderDao.createBinder(binder);
            return new ResponseEntity<>(newBinder, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating binder", e);
        }
    }

    // Create Binder for User (Admin only)
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

    // Simplified admin-only view of all binders
    // Prevents massive results by clearing the list of cards first
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


    // Get current user's binders
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

    // Get Binder by ID (Authenticated user or Admin)
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

    // todo: returning 403 for admin
    // Get Binders by User ID (Authenticated user or Admin)
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

    // Get Cards in Binder (Authenticated user or Admin)
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

    // Rename Binder (Admin only)
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

    // Delete Binder (Authenticated user or Admin)
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

    // todo: doesn't seem to map image url correctly
    // Add Card to Binder (Authenticated user or Admin)
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

    // Remove Card from Binder (Authenticated user or Admin)
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
