package com.pokebinderapp.util;

import com.pokebinderapp.model.Card;
import com.pokebinderapp.model.dto.BinderCardDto;
import com.pokebinderapp.model.dto.CardDto;
import com.pokebinderapp.model.dto.TcgplayerDto;
import com.pokebinderapp.model.dto.CardmarketDto;
import com.pokebinderapp.model.dto.PriceDto;
import com.pokebinderapp.model.dto.CardmarketPricesDto;
import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class CardMapper {

    private static final Logger logger = Logger.getLogger(CardMapper.class.getName());

    // Maps an API CardDto to a simplified domain Card for binder storage.
    public static Card mapCardDtoToDomain(CardDto dto) {
        if (dto == null) {
            return null;
        }
        Card card = new Card();
        card.setCardId(dto.getCardId());
        card.setName(dto.getName());

        // Extract prices using preferred logic (if applicable)
        BigDecimal tcgPrice = extractTcgPrice(dto.getTcgplayer());
        BigDecimal cardMarketPrice = extractCardmarketPrice(dto.getCardmarket());
        card.setPrice(!tcgPrice.equals(BigDecimal.ZERO) ? tcgPrice : cardMarketPrice);

        // Ensure image URLs are not null
        String largeImageUrl = (dto.getImages() != null) ? dto.getImages().get("large") : null;
        String smallImageUrl = (dto.getImages() != null) ? dto.getImages().get("small") : null;

        if (smallImageUrl == null || smallImageUrl.trim().isEmpty()) {
            smallImageUrl = largeImageUrl;
        }
        if (largeImageUrl == null || largeImageUrl.trim().isEmpty()) {
            largeImageUrl = smallImageUrl;
        }

        // Final fallback: Set a default image if both are null
        if (smallImageUrl == null) {
            smallImageUrl = "https://images.pokemontcg.io/sm115/13.png"; // Replace with a default image
        }
        if (largeImageUrl == null) {
            largeImageUrl = "https://images.pokemontcg.io/sm115/13_hires.png"; // Replace with a default image
        }

        card.setSmallImageUrl(smallImageUrl);
        card.setLargeImageUrl(largeImageUrl);

        return card;
    }


    // Preferred TCGPlayer extraction logic
    public static BigDecimal extractPreferredTcgPrice(TcgplayerDto tcgplayer, String preferredKey) {
        if (tcgplayer == null || tcgplayer.getPrices() == null) {
            return BigDecimal.ZERO;
        }
        Map<String, PriceDto> prices = tcgplayer.getPrices();
        if (preferredKey != null && prices.containsKey(preferredKey)) {
            PriceDto price = prices.get(preferredKey);
            if (price != null && price.getMarket() != null) {
                return price.getMarket();
            }
        }
        return extractTcgPrice(tcgplayer);
    }

    // Default extraction from TCGPlayer prices.
    public static BigDecimal extractTcgPrice(TcgplayerDto tcgplayer) {
        if (tcgplayer == null || tcgplayer.getPrices() == null) {
            return BigDecimal.ZERO;
        }
        Map<String, PriceDto> prices = tcgplayer.getPrices();
        String[] keys = {
                "normal",
                "holofoil",
                "reverseHolofoil",
                "1stEditionHolofoil",
                "1stEditionNormal",
                "unlimitedHolofoil"
        };
        for (String key : keys) {
            PriceDto price = prices.get(key);
            if (price != null && price.getMarket() != null) {
                return price.getMarket();
            }
        }
        return BigDecimal.ZERO;
    }

    // Extracts Cardmarket price using CardmarketPricesDto.
    public static BigDecimal extractCardmarketPrice(CardmarketDto cardmarket) {
        if (cardmarket == null || cardmarket.getPrices() == null) {
            return BigDecimal.ZERO;
        }
        CardmarketPricesDto prices = cardmarket.getPrices();
        if (prices.getAverageSellPrice() != null) {
            return prices.getAverageSellPrice();
        } else if (prices.getLowPrice() != null) {
            return prices.getLowPrice();
        } else if (prices.getTrendPrice() != null) {
            return prices.getTrendPrice();
        }
        return BigDecimal.ZERO;
    }

    // Converts a domain Card to a BinderCardDto for storage.
    public static BinderCardDto mapCardToBinderCardDto(Card card) {
        if (card == null) {
            return null;
        }
        int quantity = Math.max(card.getQuantity(), 1);
        return new BinderCardDto(
                card.getCardId(),
                card.getName(),
                card.getSmallImageUrl(),
                card.getLargeImageUrl(),
                card.getPrice(),
                quantity);
    }

    // Maps a SqlRowSet row from binder_cards to a domain Card.
    public static Card mapRowToCard(SqlRowSet rs) {
        Card card = new Card();
        card.setCardId(rs.getString("card_id"));
        card.setName(rs.getString("name"));
        card.setPrice(rs.getBigDecimal("price"));
        card.setSmallImageUrl(rs.getString("small_image_url"));
        card.setLargeImageUrl(rs.getString("large_image_url"));
        card.setQuantity(rs.getInt("quantity"));
        return card;
    }

}
