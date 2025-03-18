package com.pokebinderapp.controller;

import com.pokebinderapp.model.dto.CardDto;
import com.pokebinderapp.services.PokemonServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private static final Logger logger = Logger.getLogger(CardController.class.getName());
    private final PokemonServiceInterface pokemonService;

    @Autowired
    public CardController(@Qualifier("pokemonApiService") PokemonServiceInterface pokemonService){
        this.pokemonService = pokemonService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCards(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            // Default: 20 cards per page of search results
            @RequestParam(defaultValue = "20") int pageSize) {

        try {
            // Fetch all cards
            List<CardDto> allCards = pokemonService.searchCards(query);

            if (allCards == null || allCards.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cards found for query: " + query);
            }

            // Calculate total pages
            int totalResults = allCards.size();
            int totalPages = (int) Math.ceil((double) totalResults / pageSize);

            // Get paginated results
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, totalResults);
            List<CardDto> paginatedCards = allCards.subList(fromIndex, toIndex);

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("cards", paginatedCards);
            response.put("currentPage", page);
            response.put("totalPages", totalPages);
            response.put("totalResults", totalResults);
            response.put("pageSize", pageSize);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warning("Failed to search cards for query: " + query + " - " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to search cards");
        }
    }


    @GetMapping("/{cardId}")
    public CardDto getCardById(@PathVariable String cardId){
        try {
            CardDto card = pokemonService.getCardDetails(cardId);
            if (card == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found.");
            }
            return card;
        } catch (Exception e){
            logger.warning("Failed getting card by ID: " + cardId + " - " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed getting card by ID.");
        }
    }
}
