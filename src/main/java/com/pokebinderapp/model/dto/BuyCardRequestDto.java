package com.pokebinderapp.model.dto;

import java.math.BigDecimal;

public class BuyCardRequestDto {
    private int binderId;
    private String cardId;
    private BigDecimal price;
    private int userId;
    private String preferredPriceKey; // Optional; can be null if not provided

    // Getters and Setters
    public int getBinderId() {
        return binderId;
    }
    public void setBinderId(int binderId) {
        this.binderId = binderId;
    }
    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getPreferredPriceKey() {
        return preferredPriceKey;
    }
    public void setPreferredPriceKey(String preferredPriceKey) {
        this.preferredPriceKey = preferredPriceKey;
    }
}
