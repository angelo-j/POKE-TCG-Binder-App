package com.pokebinderapp.model;

import java.util.List;

public class Binder {
    private int binderId;
    private String name;
    private int userId;
    private List<Card> cards;

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
        this.cards = cards;
    }
}
