package com.databinder.scrapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.entities.Printing;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.RarityRepository;
import com.databinder.core.services.PrintingService;
import com.databinder.core.services.RarityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Component
public class CardmarketUrlBuilder {

    @Value("${cardmarket.base-url}")
    private String baseUrl;
    
    private final PrintingService printingService;

    public String buildSinglesUrl(Game game, String setName, String cardName, String rarity) {

        List<Printing> printings =
                printingService.getAllPrintingsFromCardAndSet(cardName, setName, game);

        String slug = formatSlug(cardName);

        System.out.println(printings.size());
        
        if (printings.size() > 1) {

            int version = 1;

            for (int i = 0; i < printings.size(); i++) {
                if (printings.get(i).getRarity().equalsIgnoreCase(rarity)) {
                    version = i + 1;
                    break;
                }
            }

            slug += "-V" + version + "-" + formatSlug(rarity);
        }

        return String.format("%s/%s/Products/Singles/%s/%s",
                baseUrl,
                game.getCardmarketPath(),
                formatSlug(setName),
                slug);
    }

    private static String formatSlug(String value) {
        return value
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "-");
    }
}
