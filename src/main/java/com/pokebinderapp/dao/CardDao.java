package com.pokebinderapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokebinderapp.model.Card;
import java.math.BigDecimal;
import java.util.List;

public interface CardDao {

    Card getCardById(String cardId) throws JsonProcessingException;

    List<Card> getCardsInBinder(int binderId);

    boolean addCardToBinder(int binderId, Card card);

    boolean removeCardFromBinder(int binderId, String cardId);

    boolean buyCardToBinder(int binderId, String cardId, BigDecimal price, int userId, String preferredPriceKey) throws JsonProcessingException;

    boolean sellCardFromBinder(int binderId, String cardId, BigDecimal price, int userId);

    boolean updateCardQuantity(int binderId, String cardId, int quantity);  // Sets specific quantity

    int getCardQuantity(int binderId, String cardId);  // Gets current quantity of a card
}
