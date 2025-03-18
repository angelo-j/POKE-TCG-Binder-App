package com.pokebinderapp.dao;

import com.pokebinderapp.model.Card;
import com.pokebinderapp.model.dto.CardDto;
import com.pokebinderapp.model.dto.BinderCardDto;
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
    public List<Card> getCardsInBinder(int binderId) {
        String sql = "SELECT * FROM binder_cards WHERE binder_id = ?";
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

        // Convert API response to DB-ready format if needed
        BinderCardDto dto = CardMapper.mapCardToBinderCardDto(card);
        if (dto == null) {
            return false;
        }

        // Check if the card already exists in the binder
        String checkSql = "SELECT quantity FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(checkSql, binderId, dto.getCardId());

        if (result.next()) {
            // Card already exists, increase quantity
            String updateSql = "UPDATE binder_cards SET quantity = quantity + 1 WHERE binder_id = ? AND card_id = ?";
            jdbcTemplate.update(updateSql, binderId, dto.getCardId());
        } else {
            // Insert new card entry
            String insertSql = "INSERT INTO binder_cards (binder_id, card_id, name, price, small_image_url, large_image_url, quantity) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 1)";
            jdbcTemplate.update(insertSql, binderId, dto.getCardId(), dto.getName(),
                    dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO,
                    dto.getSmallImageUrl(), dto.getLargeImageUrl());
        }
        return true;
    }

    @Transactional
    @Override
    public boolean removeCardFromBinder(int binderId, String cardId) {
        String checkSql = "SELECT quantity FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(checkSql, binderId, cardId);
        if (result.next()) {
            int quantity = result.getInt("quantity");
            if (quantity > 1) {
                // Decrement quantity
                String updateSql = "UPDATE binder_cards SET quantity = quantity - 1 WHERE binder_id = ? AND card_id = ?";
                jdbcTemplate.update(updateSql, binderId, cardId);
            } else {
                // Remove entry
                String deleteSql = "DELETE FROM binder_cards WHERE binder_id = ? AND card_id = ?";
                jdbcTemplate.update(deleteSql, binderId, cardId);
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean buyCardToBinder(int binderId, String cardId, BigDecimal price, int userId, String preferredPriceKey) {
        // Deduct money from user if they have enough
        String sql = "UPDATE users SET money = money - ? WHERE user_id = ? AND money >= ?";
        int rowsAffected = jdbcTemplate.update(sql, price, userId, price);
        if (rowsAffected == 0) {
            return false; // Insufficient funds
        }

        // Get full card details from API
        CardDto cardDto = pokemonApiService.getCardDetails(cardId);
        if (cardDto == null) {
            // Refund the money if API fails
            jdbcTemplate.update("UPDATE users SET money = money + ? WHERE user_id = ?", price, userId);
            return false;
        }

        // Convert and add card to binder
        Card card = CardMapper.mapCardDtoToDomain(cardDto);
        card.setPrice(price); // Use transaction price
        return addCardToBinder(binderId, card);
    }

    @Transactional
    @Override
    public boolean sellCardFromBinder(int binderId, String cardId, BigDecimal price, int binderOwnerId) {
        // Get stored price
        String priceSql = "SELECT price FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(priceSql, binderId, cardId);
        BigDecimal storedPrice = BigDecimal.ZERO;
        if (rs.next()) {
            storedPrice = rs.getBigDecimal("price");
        }

        // Remove from binder
        if (removeCardFromBinder(binderId, cardId)) {
            // Add money to seller
            String updateSql = "UPDATE users SET money = money + ? WHERE user_id = ?";
            jdbcTemplate.update(updateSql, storedPrice, binderOwnerId);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateCardQuantity(int binderId, String cardId, int quantity) {
        String sql = "UPDATE binder_cards SET quantity = ? WHERE binder_id = ? AND card_id = ?";
        return jdbcTemplate.update(sql, quantity, binderId, cardId) > 0;
    }

    @Override
    public int getCardQuantity(int binderId, String cardId) {
        String sql = "SELECT quantity FROM binder_cards WHERE binder_id = ? AND card_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, binderId, cardId);
        return result.next() ? result.getInt("quantity") : 0;
    }
}
