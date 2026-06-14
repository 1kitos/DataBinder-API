package com.databinder.scrapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.entities.Printing;

@Component
public class CardmarketUrlBuilder {

	@Value("${cardmarket.base-url}")
    private String baseUrl;

    public String buildSinglesUrl(String game, String setName, String cardName) {
        return String.format("%s/%s/Products/Singles/%s/%s",
        	baseUrl,
            formatSlug(game),
            formatSlug(setName),
            formatSlug(cardName));
    }

    private static String formatSlug(String value) {
        return value
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "-");
    }


}
