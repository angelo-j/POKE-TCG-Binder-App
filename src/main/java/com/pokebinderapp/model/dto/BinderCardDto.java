package com.pokebinderapp.model.dto;

import com.pokebinderapp.model.Card;

import java.math.BigDecimal;

public class BinderCardDto {
    private String cardId;
    private String name;
    private String smallImageUrl;
    private String largeImageUrl;
    private BigDecimal price;
    private int quantity = 1; // Default quantity

    public BinderCardDto() { }

    public BinderCardDto(String cardId, String name, String smallImageUrl, String largeImageUrl, BigDecimal price, int quantity) {
        this.cardId = cardId;
        this.name = name;
        this.smallImageUrl = smallImageUrl;
        this.largeImageUrl = largeImageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public static BinderCardDto mapCardToBinderCardDto(Card card) {
        if (card == null) return null;
        int quantity = Math.max(card.getQuantity(), 1);
        String smallImageUrl = card.getSmallImageUrl();
        String largeImageUrl = card.getLargeImageUrl();
        return new BinderCardDto(card.getCardId(), card.getName(), smallImageUrl, largeImageUrl, card.getPrice(), quantity);
    }


    // Getters and Setters
    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }
    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getLargeImageUrl() { return largeImageUrl; }
    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
