package com.pokebinderapp.model;

import java.math.BigDecimal;

public class Card {
    private String cardId;
    private String name;
    private BigDecimal price;
    private String smallImageUrl;
    private String largeImageUrl;
    private int quantity = 1;  // Default value

    public Card() { }

    public Card(String cardId, String name, BigDecimal price, String smallImageUrl, String largeImageUrl, int quantity) {
        this.cardId = cardId;
        this.name = name;
        this.price = price;
        this.smallImageUrl = smallImageUrl;
        this.largeImageUrl = largeImageUrl;
        this.quantity = quantity;
    }

    // Getters and setters
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
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getSmallImageUrl() {
        return smallImageUrl;
    }
    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }
    public String getLargeImageUrl() {
        return largeImageUrl;
    }
    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
