package com.pokebinderapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TcgplayerDto {

    private String url;
    private String updatedAt;

    @JsonProperty("prices")
    private Map<String, PriceDto> prices; // ✅ Correct: Prices are stored in a map.

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public Map<String, PriceDto> getPrices() { return prices; }
    public void setPrices(Map<String, PriceDto> prices) { this.prices = prices; }
}
