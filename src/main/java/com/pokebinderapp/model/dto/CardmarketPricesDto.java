package com.pokebinderapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardmarketPricesDto {

    private BigDecimal averageSellPrice;
    private BigDecimal lowPrice;
    private BigDecimal trendPrice;
    private BigDecimal germanProLow;
    private BigDecimal suggestedPrice;
    private BigDecimal reverseHoloSell;
    private BigDecimal reverseHoloLow;
    private BigDecimal reverseHoloTrend;
    private BigDecimal lowPriceExPlus;
    private BigDecimal avg1;
    private BigDecimal avg7;
    private BigDecimal avg30;
    private BigDecimal reverseHoloAvg1;
    private BigDecimal reverseHoloAvg7;
    private BigDecimal reverseHoloAvg30;

    public BigDecimal getAverageSellPrice() {
        return averageSellPrice;
    }

    public void setAverageSellPrice(BigDecimal averageSellPrice) {
        this.averageSellPrice = averageSellPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getTrendPrice() {
        return trendPrice;
    }

    public void setTrendPrice(BigDecimal trendPrice) {
        this.trendPrice = trendPrice;
    }

    public BigDecimal getGermanProLow() {
        return germanProLow;
    }

    public void setGermanProLow(BigDecimal germanProLow) {
        this.germanProLow = germanProLow;
    }

    public BigDecimal getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(BigDecimal suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public BigDecimal getReverseHoloSell() {
        return reverseHoloSell;
    }

    public void setReverseHoloSell(BigDecimal reverseHoloSell) {
        this.reverseHoloSell = reverseHoloSell;
    }

    public BigDecimal getReverseHoloLow() {
        return reverseHoloLow;
    }

    public void setReverseHoloLow(BigDecimal reverseHoloLow) {
        this.reverseHoloLow = reverseHoloLow;
    }

    public BigDecimal getReverseHoloTrend() {
        return reverseHoloTrend;
    }

    public void setReverseHoloTrend(BigDecimal reverseHoloTrend) {
        this.reverseHoloTrend = reverseHoloTrend;
    }

    public BigDecimal getLowPriceExPlus() {
        return lowPriceExPlus;
    }

    public void setLowPriceExPlus(BigDecimal lowPriceExPlus) {
        this.lowPriceExPlus = lowPriceExPlus;
    }

    public BigDecimal getAvg1() {
        return avg1;
    }

    public void setAvg1(BigDecimal avg1) {
        this.avg1 = avg1;
    }

    public BigDecimal getAvg7() {
        return avg7;
    }

    public void setAvg7(BigDecimal avg7) {
        this.avg7 = avg7;
    }

    public BigDecimal getAvg30() {
        return avg30;
    }

    public void setAvg30(BigDecimal avg30) {
        this.avg30 = avg30;
    }

    public BigDecimal getReverseHoloAvg1() {
        return reverseHoloAvg1;
    }

    public void setReverseHoloAvg1(BigDecimal reverseHoloAvg1) {
        this.reverseHoloAvg1 = reverseHoloAvg1;
    }

    public BigDecimal getReverseHoloAvg7() {
        return reverseHoloAvg7;
    }

    public void setReverseHoloAvg7(BigDecimal reverseHoloAvg7) {
        this.reverseHoloAvg7 = reverseHoloAvg7;
    }

    public BigDecimal getReverseHoloAvg30() {
        return reverseHoloAvg30;
    }

    public void setReverseHoloAvg30(BigDecimal reverseHoloAvg30) {
        this.reverseHoloAvg30 = reverseHoloAvg30;
    }
}
