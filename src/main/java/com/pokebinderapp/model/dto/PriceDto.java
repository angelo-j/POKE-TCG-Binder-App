package com.pokebinderapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceDto {

    private BigDecimal low;
    private BigDecimal mid;
    private BigDecimal high;
    private BigDecimal market;
    private BigDecimal directLow;

    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }
    public BigDecimal getMid() { return mid; }
    public void setMid(BigDecimal mid) { this.mid = mid; }
    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }
    public BigDecimal getMarket() { return market; }
    public void setMarket(BigDecimal market) { this.market = market; }
    public BigDecimal getDirectLow() { return directLow; }
    public void setDirectLow(BigDecimal directLow) { this.directLow = directLow; }
}
