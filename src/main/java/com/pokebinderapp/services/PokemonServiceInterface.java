package com.pokebinderapp.services;

import com.pokebinderapp.model.dto.CardDto;

import java.util.List;

public interface PokemonServiceInterface {

    List<CardDto> searchCards(String query);

    CardDto getCardDetails(String cardId);

}
