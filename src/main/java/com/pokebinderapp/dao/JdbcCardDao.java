package com.pokebinderapp.dao;

import com.pokebinderapp.model.Card;
import com.pokebinderapp.model.dto.BinderCardDto;
import com.pokebinderapp.model.dto.CardDto;
import com.pokebinderapp.services.PokemonApiService;
import com.pokebinderapp.util.CardMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;


@Repository
public class JdbcCardDao implements CardDao {

    private final JdbcTemplate jdbcTemplate;
    private final PokemonApiService pokemonApiService;

    @Autowired
    public JdbcCardDao(JdbcTemplate jdbcTemplate, PokemonApiService pokemonApiService) {
        this.jdbcTemplate = jdbcTemplate;
        this.pokemonApiService = pokemonApiService;
    }

    @Override
    public Card getCardById(String cardId) throws JsonProcessingException {
        CardDto cardDto = pokemonApiService.getCardDetails(cardId);
        return CardMapper.mapCardDtoToDomain(cardDto);
    }

    @Override
    public List<Card> getCardsInBinder(int binderId) {
        String sql = "SELECT card_id, name, price, small_image_url, large_image_url, quantity FROM binder_cards WHERE binder_id = ?";
        List<Card> cards = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, binderId);
        while (results.next()) {
            cards.add(CardMapper.mapRowToCard(results));
        }
        return cards;
    }

    @Transactional
    @Override
    public boolean addCardToBinder(int binderId, Card card) {
        if (card == null || card.getCardId() == null) {
            return false;
        }
        if (card.getName() == null || card.getPrice() == null) {
            // Convert the full details (CardDto) into a domain Card
            card = CardMapper.mapCardDtoToDomain(pokemonApiService.getCardDetails(card.getCardId()));
            if (card == null) {
                return false;
            }
        }
        BinderCardDto dto = CardMapper.mapCardToBinderCardDto(card);
        String checkSql = "SELECT quantity FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(checkSql, binderId, dto.getCardId());
        if (result.next()) {
            String updateSql = "UPDATE binder_cards SET quantity = quantity + ? WHERE binder_id = ? AND card_id = ?";
            jdbcTemplate.update(updateSql, dto.getQuantity(), binderId, dto.getCardId());
        } else {
            int quantity = (dto.getQuantity() > 0) ? dto.getQuantity() : 1;
            String insertSql = "INSERT INTO binder_cards (binder_id, card_id, name, price, small_image_url, large_image_url, quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, binderId, dto.getCardId(), dto.getName(),
                    (dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO), dto.getSmallImageUrl(), dto.getLargeImageUrl(), quantity);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean buyCardToBinder(int binderId, String cardId, BigDecimal price, int userId, String preferredPriceKey) {
        // Deduct money from the user's account
        String sql = "UPDATE users SET money = money - ? WHERE user_id = ? AND money >= ?";
        int rowsAffected = jdbcTemplate.update(sql,
                (price != null ? price : BigDecimal.ZERO),
                userId,
                (price != null ? price : BigDecimal.ZERO));
        if (rowsAffected > 0) {
            // Retrieve full card details from the API
            CardDto cardDto = pokemonApiService.getCardDetails(cardId);
            if (cardDto == null) {
                jdbcTemplate.update("UPDATE users SET money = money + ? WHERE user_id = ?",
                        (price != null ? price : BigDecimal.ZERO), userId);
                return false;
            }
            // Use the preferred price key if provided
            BigDecimal tcgPrice = (preferredPriceKey != null && !preferredPriceKey.isEmpty()) ?
                    CardMapper.extractPreferredTcgPrice(cardDto.getTcgplayer(), preferredPriceKey) :
                    CardMapper.extractTcgPrice(cardDto.getTcgplayer());
            BigDecimal cardMarketPrice = CardMapper.extractCardmarketPrice(cardDto.getCardmarket());
            BigDecimal finalPrice = !tcgPrice.equals(BigDecimal.ZERO) ? tcgPrice : cardMarketPrice;
            Card card = CardMapper.mapCardDtoToDomain(cardDto);
            card.setPrice(finalPrice);
            if (card != null && card.getName() != null) {
                return addCardToBinder(binderId, card);
            } else {
                // Refund if card details weren't retrieved successfully
                jdbcTemplate.update("UPDATE users SET money = money + ? WHERE user_id = ?",
                        (price != null ? price : BigDecimal.ZERO), userId);
                return false;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public boolean removeCardFromBinder(int binderId, String cardId) {
        String checkSql = "SELECT quantity FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(checkSql, binderId, cardId);
        if (result.next()) {
            int quantity = result.getInt("quantity");
            if (quantity > 1) {
                String updateSql = "UPDATE binder_cards SET quantity = quantity - 1 WHERE binder_id = ? AND card_id = ?";
                jdbcTemplate.update(updateSql, binderId, cardId);
            } else {
                String deleteSql = "DELETE FROM binder_cards WHERE binder_id = ? AND card_id = ?";
                jdbcTemplate.update(deleteSql, binderId, cardId);
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean sellCardFromBinder(int binderId, String cardId, BigDecimal price, int binderOwnerId) {
        // Retrieve the stored price from the binder_cards table
        String priceSql = "SELECT price FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(priceSql, binderId, cardId);
        BigDecimal storedPrice = BigDecimal.ZERO;
        if (rs.next()) {
            storedPrice = rs.getBigDecimal("price");
        }

        // Remove the card from the binder (this handles quantity changes)
        if (removeCardFromBinder(binderId, cardId)) {
            // Update the binder owner's money using the stored price
            String updateSql = "UPDATE users SET money = money + ? WHERE user_id = ?";
            jdbcTemplate.update(updateSql, (storedPrice != null ? storedPrice : BigDecimal.ZERO), binderOwnerId);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateCardQuantity(int binderId, String cardId, int quantity) {
        String sql = "UPDATE binder_cards SET quantity = ? WHERE binder_id = ? AND card_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, quantity, binderId, cardId);
        return rowsAffected > 0;
    }

    @Override
    public int getCardQuantity(int binderId, String cardId) {
        String sql = "SELECT quantity FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, binderId, cardId);
        if (result.next()) {
            return result.getInt("quantity");
        }
        return 0;
    }
}
