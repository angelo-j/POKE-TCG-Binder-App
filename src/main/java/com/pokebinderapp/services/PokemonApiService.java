package com.pokebinderapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokebinderapp.config.PokemonApiConfig;
import com.pokebinderapp.model.dto.CardDto;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service("pokemonApiService")
public class PokemonApiService implements PokemonServiceInterface {

    private static final Logger logger = Logger.getLogger(PokemonApiService.class.getName());

    @Autowired
    private PokemonApiConfig config;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Searches for cards matching the given query and returns a list of full detailed CardDto objects.
     */
    @Override
    public List<CardDto> searchCards(String query) {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = config.getApiUrl() + "/cards?q=name:" + encodedQuery;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", config.getApiKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            logger.info("API Response: " + response.getBody());

            JsonNode root = objectMapper.readTree(response.getBody()).get("data");
            List<CardDto> cardDtos = new ArrayList<>();
            if (root != null && root.isArray()) {
                for (JsonNode node : root) {
                    CardDto cardDto = objectMapper.treeToValue(node, CardDto.class);
                    cardDtos.add(cardDto);
                }
            }
            return cardDtos;
        } catch (RestClientException | JsonProcessingException e) {
            logger.warning("Error searching cards: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves full detailed information for a specific card as a CardDto.
     */
    @Override
    public CardDto getCardDetails(String cardId) {
        String encodedCardId = URLEncoder.encode(cardId, StandardCharsets.UTF_8);
        String url = config.getApiUrl() + "/cards/" + encodedCardId;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", config.getApiKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            logger.info("API Response: " + response.getBody());

            JsonNode root = objectMapper.readTree(response.getBody()).get("data");
            if (root != null && root.isObject()) {
                return objectMapper.treeToValue(root, CardDto.class);
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.warning("Error getting card details: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a detailed list of CardDto objects matching the query.
     * This method delegates to searchCards(), but you can add extra processing if needed.
     */
    @Override
    public List<CardDto> searchCardsDetailed(String query) {
        return searchCards(query);
    }
}
