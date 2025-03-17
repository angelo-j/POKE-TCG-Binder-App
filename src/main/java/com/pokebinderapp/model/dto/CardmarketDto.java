package com.pokebinderapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardmarketDto {

    private String url;
    private String updatedAt;

    @JsonProperty("prices")
    private CardmarketPricesDto prices; // Prices stored as an object.

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public CardmarketPricesDto getPrices() { return prices; }
    public void setPrices(CardmarketPricesDto prices) { this.prices = prices; }
}
