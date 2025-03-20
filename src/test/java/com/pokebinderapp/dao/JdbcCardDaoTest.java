package com.pokebinderapp.dao;

import com.pokebinderapp.model.Binder;
import com.pokebinderapp.model.Card;
import com.pokebinderapp.model.dto.BinderCardDto;
import com.pokebinderapp.model.dto.CardDto;
import com.pokebinderapp.services.PokemonApiService;
import com.pokebinderapp.util.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JdbcCardDaoTest extends BaseDaoTest {

    @Mock
    private PokemonApiService pokemonApiService;

    private JdbcCardDao cardDao;
    private JdbcBinderDao binderDao;
    private JdbcUserDao userDao;

    @BeforeEach
    public void setup() {
        // Ensure mocks are initialized before DAOs
        MockitoAnnotations.openMocks(this);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.cardDao = new JdbcCardDao(jdbcTemplate, pokemonApiService);
        this.binderDao = new JdbcBinderDao(jdbcTemplate, cardDao);
        this.userDao = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void testAddCardToBinder_Success() {
        Binder binder = new Binder();
        binder.setName("Test Binder");
        binder.setUserId(1);
        binder = binderDao.createBinder(binder);

        Card card = new Card();
        card.setCardId("xy7-54");
        card.setName("Rayquaza-EX");
        card.setPrice(new BigDecimal("5.00"));
        card.setSmallImageUrl("small_img_url");
        card.setLargeImageUrl("large_img_url");

        BinderCardDto binderCardDto = CardMapper.mapCardToBinderCardDto(card);

        boolean added = cardDao.addCardToBinder(binder.getBinderId(), card);
        assertTrue(added);

        List<Card> cards = cardDao.getCardsInBinder(binder.getBinderId());
        assertFalse(cards.isEmpty());
        assertEquals(binderCardDto.getName(), cards.get(0).getName());
    }

    @Test
    public void testRemoveCardFromBinder() {
        Binder binder = new Binder();
        binder.setName("Test Binder");
        binder.setUserId(1);
        binder = binderDao.createBinder(binder);

        Card card = new Card();
        card.setCardId("xy7-54");
        card.setName("Rayquaza-EX");
        card.setPrice(new BigDecimal("5.00"));
        card.setSmallImageUrl("small_img_url");
        card.setLargeImageUrl("large_img_url");

        cardDao.addCardToBinder(binder.getBinderId(), card);
        boolean removed = cardDao.removeCardFromBinder(binder.getBinderId(), card.getCardId());

        assertTrue(removed);
        assertTrue(cardDao.getCardsInBinder(binder.getBinderId()).isEmpty());
    }

    @Test
    public void testBuyCardToBinder_Success() {
        Binder binder = new Binder();
        binder.setName("Buying Binder");
        binder.setUserId(1);
        binder = binderDao.createBinder(binder);

        BigDecimal userMoneyBefore = userDao.getMoney(1);
        BigDecimal cardPrice = new BigDecimal("10.00");

        // Mock API response
        CardDto mockCardDto = new CardDto();
        mockCardDto.setCardId("xy7-54");
        mockCardDto.setName("Rayquaza-EX");
        when(pokemonApiService.getCardDetails("xy7-54")).thenReturn(mockCardDto);

        boolean bought = cardDao.buyCardToBinder(binder.getBinderId(), "xy7-54", cardPrice, 1, null);

        assertTrue(bought);
        assertEquals(userMoneyBefore.subtract(cardPrice), userDao.getMoney(1));
    }

    @Test
    public void buyCard_NotEnoughMoney_ShouldFail() {
        Binder binder = new Binder();
        binder.setName("Buying Binder");
        binder.setUserId(1);
        binder = binderDao.createBinder(binder);

        userDao.updateMoney(1, new BigDecimal("5.00")); // User has only $5

        boolean bought = cardDao.buyCardToBinder(binder.getBinderId(), "xy7-54", new BigDecimal("10.00"), 1, null);

        assertFalse(bought, "Expected buyCardToBinder() to fail due to insufficient funds.");
    }


    @Test
    public void testSellCardFromBinder_Success() {
        Binder binder = new Binder();
        binder.setName("Selling Binder");
        binder.setUserId(1);
        binder = binderDao.createBinder(binder);

        Card card = new Card();
        card.setCardId("xy7-54");
        card.setName("Rayquaza-EX");
        card.setPrice(new BigDecimal("15.00"));
        card.setSmallImageUrl("small_img_url");
        card.setLargeImageUrl("large_img_url");

        cardDao.addCardToBinder(binder.getBinderId(), card);
        BigDecimal userMoneyBefore = userDao.getMoney(1);

        boolean sold = cardDao.sellCardFromBinder(binder.getBinderId(), card.getCardId(), card.getPrice(), 1);

        assertTrue(sold);
        assertEquals(userMoneyBefore.add(card.getPrice()), userDao.getMoney(1));
    }

    @Test
    public void sellCard_NotEnoughQuantity_ShouldFail() {
        Binder binder = new Binder();
        binder.setName("Selling Binder");
        binder.setUserId(1);
        binder = binderDao.createBinder(binder);

        Card card = new Card();
        card.setCardId("xy7-54");
        card.setName("Rayquaza-EX");
        card.setSmallImageUrl("smallTestUrl");
        card.setLargeImageUrl("largeTestUrl");
        card.setPrice(new BigDecimal("15.00"));
        card.setQuantity(1);
        cardDao.addCardToBinder(binder.getBinderId(), card);

        boolean sold = cardDao.sellCardFromBinder(binder.getBinderId(), card.getCardId(), new BigDecimal("15.00"), 1);
        assertTrue(sold, "Selling one card should succeed.");

        // Try selling again (should fail)
        boolean soldAgain = cardDao.sellCardFromBinder(binder.getBinderId(), card.getCardId(), new BigDecimal("15.00"), 1);
        assertFalse(soldAgain, "Selling a card without enough quantity should fail.");
    }

}
