package com.pokebinderapp.model.dto;

import com.pokebinderapp.model.Binder;
import com.pokebinderapp.model.Card;

import java.util.ArrayList;
import java.util.List;

public class BinderDto {
    private int binderId;
    private String name;
    private int userId;
    private List<Card> cards = new ArrayList<>();

    public BinderDto() {}

    public BinderDto(int binderId, String name, int userId, List<Card> cards) {
        this.binderId = binderId;
        this.name = name;
        this.userId = userId;
        this.cards = cards != null ? cards : new ArrayList<>();
    }

    public int getBinderId() {
        return binderId;
    }

    public void setBinderId(int binderId) {
        this.binderId = binderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards != null ? cards : new ArrayList<>();
    }

    /**
     * Adds a card to the binder. If the card already exists, updates the quantity.
     */
    public void addCardToBinder(Card card) {
        for (Card existingCard : cards) {
            if (existingCard.getCardId().equals(card.getCardId())) {
                existingCard.setQuantity(existingCard.getQuantity() + card.getQuantity());
                return;
            }
        }
        cards.add(card);
    }

    /**
     * Removes one copy of the card from the binder. If quantity is 1, it removes the card entirely.
     */
    public void removeCardFromBinder(String cardId) {
        cards.removeIf(card -> {
            if (card.getCardId().equals(cardId)) {
                if (card.getQuantity() > 1) {
                    card.setQuantity(card.getQuantity() - 1);
                    return false;
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Updates the quantity for a specific card. If quantity is 0, the card is removed.
     */
    public void updateCardQuantity(String cardId, int quantity) {
        if (quantity < 0) return;

        for (Card card : cards) {
            if (card.getCardId().equals(cardId)) {
                if (quantity == 0) {
                    cards.remove(card);
                } else {
                    card.setQuantity(quantity);
                }
                return;
            }
        }
    }

    /**
     * Converts a domain Binder into a BinderDto for API responses.
     */
    public static BinderDto fromBinder(Binder binder) {
        return new BinderDto(
                binder.getBinderId(),
                binder.getName(),
                binder.getUserId(),
                binder.getCards()
        );
    }
}
