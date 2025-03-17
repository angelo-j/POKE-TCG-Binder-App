package com.pokebinderapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokebinderapp.model.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcCardDaoTest extends BaseDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcCardDao cardDao;

    @BeforeEach
    public void setup() {
        String insertUserSql = "INSERT INTO users (username, password_hash, role, money) VALUES ('testuser', 'password', 'ROLE_USER', 100.00)";
        jdbcTemplate.update(insertUserSql);

        String insertBinderSql = "INSERT INTO binder (name, user_id) VALUES ('Test Binder', 1)";
        jdbcTemplate.update(insertBinderSql);
    }

    @Test
    void getCardById_ShouldReturnCard() throws JsonProcessingException {
        // Arrange
        String defaultSmallUrl = "https://images.pokemontcg.io/base3/5.png";
        String defaultLargeUrl = "https://images.pokemontcg.io/base3/5_hires.png";

        String insertCardSql = "INSERT INTO binder_cards (binder_id, card_id, name, price, small_image_url, large_image_url, quantity) " +
                "VALUES (1, 'xy7-54', 'Mewtwo', 10.00, ?, ?, 1)";
        jdbcTemplate.update(insertCardSql, defaultSmallUrl, defaultLargeUrl);

        // Act
        Card card = cardDao.getCardById("xy7-54");

        // Assert
        assertNotNull(card);
        assertEquals("Mewtwo", card.getName());
        assertEquals(new BigDecimal("10.00"), card.getPrice());
        assertEquals(defaultSmallUrl, card.getSmallImageUrl());
        assertEquals(defaultLargeUrl, card.getLargeImageUrl());

        // Cleanup
        jdbcTemplate.update("TRUNCATE TABLE binder_cards RESTART IDENTITY CASCADE;");
    }

    @Test
    void updateCardQuantity_ShouldUpdateQuantity() throws JsonProcessingException {
        // Arrange
        String insertCardSql = "INSERT INTO binder_cards (binder_id, card_id, name, price, small_image_url, large_image_url, quantity) " +
                "VALUES (1, 'xy7-54', 'Mewtwo', 10.00, 'https://images.pokemontcg.io/base3/5.png', 'https://images.pokemontcg.io/base3/5_hires.png', 1)";
        jdbcTemplate.update(insertCardSql);

        // Act
        boolean updated = cardDao.updateCardQuantity(1, "xy7-54", 5);
        Card updatedCard = cardDao.getCardById("xy7-54");

        // Assert
        assertTrue(updated);
        assertNotNull(updatedCard);
        assertEquals(5, updatedCard.getQuantity());

        // Cleanup
        jdbcTemplate.update("TRUNCATE TABLE binder_cards RESTART IDENTITY CASCADE;");
    }

    @Test
    void addCardToBinder_ShouldInsertOrIncrementQuantity() throws JsonProcessingException {
        // Arrange
        Card card = new Card();
        card.setCardId("test-123");
        card.setName("Test Card");
        card.setPrice(new BigDecimal("5.00"));
        card.setQuantity(1);
        card.setSmallImageUrl("https://images.pokemontcg.io/base3/5.png");
        card.setLargeImageUrl("https://images.pokemontcg.io/base3/5_hires.png");

        // Act
        boolean addedFirstTime = cardDao.addCardToBinder(1, card);
        boolean addedSecondTime = cardDao.addCardToBinder(1, card);
        int quantity = cardDao.getCardQuantity(1, "test-123");

        // Assert
        assertTrue(addedFirstTime);
        assertTrue(addedSecondTime);
        assertEquals(2, quantity);

        // Cleanup
        jdbcTemplate.update("TRUNCATE TABLE binder_cards RESTART IDENTITY CASCADE;");
    }

    @Test
    void removeCardFromBinder_ShouldDecrementOrDeleteCard() throws JsonProcessingException {
        // Arrange
        String insertSql = "INSERT INTO binder_cards (binder_id, card_id, name, price, small_image_url, large_image_url, quantity) " +
                "VALUES (1, 'test-456', 'Test Remove Card', 5.00, 'https://images.pokemontcg.io/base3/5.png', 'https://images.pokemontcg.io/base3/5_hires.png', 2)";
        jdbcTemplate.update(insertSql);

        // Act
        boolean removedOnce = cardDao.removeCardFromBinder(1, "test-456");
        int quantityAfterOneRemove = cardDao.getCardQuantity(1, "test-456");
        boolean removedTwice = cardDao.removeCardFromBinder(1, "test-456");
        int quantityAfterTwoRemoves = cardDao.getCardQuantity(1, "test-456");

        // Assert
        assertTrue(removedOnce);
        assertEquals(1, quantityAfterOneRemove);
        assertTrue(removedTwice);
        assertEquals(0, quantityAfterTwoRemoves);

        // Cleanup
        jdbcTemplate.update("TRUNCATE TABLE binder_cards RESTART IDENTITY CASCADE;");
    }
}
