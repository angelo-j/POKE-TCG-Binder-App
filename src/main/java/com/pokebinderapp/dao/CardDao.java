package com.pokebinderapp.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pokebinderapp.model.Card;
import java.math.BigDecimal;
import java.util.List;

public interface CardDao {


    /**
     * Gets all cards in a specific binder from the database.
     */
    List<Card> getCardsInBinder(int binderId);

    /**
     * Adds a card to a binder. If the card exists, increments its quantity.
     */
    boolean addCardToBinder(int binderId, Card card);

    /**
     * Removes one instance of a card from the binder.
     * If the quantity is 1, it deletes the card entry entirely.
     */
    boolean removeCardFromBinder(int binderId, String cardId);

    /**
     * Purchases a card and adds it to the specified binder, deducting money from the user.
     */
    boolean buyCardToBinder(int binderId, String cardId, BigDecimal price, int userId, String preferredPriceKey) throws JsonProcessingException;

    /**
     * Sells a card from the binder and credits the user’s balance.
     */
    boolean sellCardFromBinder(int binderId, String cardId, BigDecimal price, int userId);

    /**
     * Updates the quantity of a card in the binder.
     */
    boolean updateCardQuantity(int binderId, String cardId, int quantity);

    /**
     * Retrieves the current quantity of a card in the binder.
     */
    int getCardQuantity(int binderId, String cardId);
}
