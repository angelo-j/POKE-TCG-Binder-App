package com.pokebinderapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDto {

    // todo: separate nested classes for better responses

    @JsonProperty("id")
    private String cardId;
    private String name;
    private String supertype;
    private List<String> subtypes;
    private String hp;
    private List<String> types;
    private String evolvesFrom;
    private List<AbilityDto> abilities;
    private List<AttackDto> attacks;
    private List<WeaknessDto> weaknesses;
    private List<String> retreatCost;
    private int convertedRetreatCost;
    private String number;
    private String artist;
    private String rarity;
    private String flavorText;
    private List<Integer> nationalPokedexNumbers;
    private Map<String, String> legalities;
    private Map<String, String> images;

    // These reference separate DTO classes
    private TcgplayerDto tcgplayer;
    private CardmarketDto cardmarket;

    // Getters and Setters
    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSupertype() { return supertype; }
    public void setSupertype(String supertype) { this.supertype = supertype; }

    public List<String> getSubtypes() { return subtypes; }
    public void setSubtypes(List<String> subtypes) { this.subtypes = subtypes; }

    public String getHp() { return hp; }
    public void setHp(String hp) { this.hp = hp; }

    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }

    public String getEvolvesFrom() { return evolvesFrom; }
    public void setEvolvesFrom(String evolvesFrom) { this.evolvesFrom = evolvesFrom; }

    public List<AbilityDto> getAbilities() { return abilities; }
    public void setAbilities(List<AbilityDto> abilities) { this.abilities = abilities; }

    public List<AttackDto> getAttacks() { return attacks; }
    public void setAttacks(List<AttackDto> attacks) { this.attacks = attacks; }

    public List<WeaknessDto> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<WeaknessDto> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getRetreatCost() { return retreatCost; }
    public void setRetreatCost(List<String> retreatCost) { this.retreatCost = retreatCost; }

    public int getConvertedRetreatCost() { return convertedRetreatCost; }
    public void setConvertedRetreatCost(int convertedRetreatCost) { this.convertedRetreatCost = convertedRetreatCost; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public String getFlavorText() { return flavorText; }
    public void setFlavorText(String flavorText) { this.flavorText = flavorText; }

    public List<Integer> getNationalPokedexNumbers() { return nationalPokedexNumbers; }
    public void setNationalPokedexNumbers(List<Integer> nationalPokedexNumbers) { this.nationalPokedexNumbers = nationalPokedexNumbers; }

    public Map<String, String> getLegalities() { return legalities; }
    public void setLegalities(Map<String, String> legalities) { this.legalities = legalities; }

    public Map<String, String> getImages() { return images; }
    public void setImages(Map<String, String> images) { this.images = images; }

    public TcgplayerDto getTcgplayer() { return tcgplayer; }
    public void setTcgplayer(TcgplayerDto tcgplayer) { this.tcgplayer = tcgplayer; }

    public CardmarketDto getCardmarket() { return cardmarket; }
    public void setCardmarket(CardmarketDto cardmarket) { this.cardmarket = cardmarket; }

    // --- Nested DTO Classes ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AbilityDto {
        private String name;
        private String text;
        private String type;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttackDto {
        private String name;
        private String text;
        private String damage;
        private List<String> cost;
        private int convertedEnergyCost;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getDamage() { return damage; }
        public void setDamage(String damage) { this.damage = damage; }

        public List<String> getCost() { return cost; }
        public void setCost(List<String> cost) { this.cost = cost; }

        public int getConvertedEnergyCost() { return convertedEnergyCost; }
        public void setConvertedEnergyCost(int convertedEnergyCost) { this.convertedEnergyCost = convertedEnergyCost; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeaknessDto {
        private String type;
        private String value;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AncientTrait {
        private String name;
        private String text;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
