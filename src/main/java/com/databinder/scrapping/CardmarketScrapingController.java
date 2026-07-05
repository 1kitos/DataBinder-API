package com.databinder.scrapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.services.PrintingService;
import com.databinder.search.services.SearchService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scraping")
@RequiredArgsConstructor
@Tag(name = "Scraping", description = "Scraping Controller")
public class CardmarketScrapingController {

    private final CardmarketScrapingService scrapingService;
    private final CardmarketUrlBuilder cardmarketUrlBuilder;

    @GetMapping("/prices")
    public ResponseEntity<CardmarketPriceData> getPrices(
            @RequestParam Game game,
            @RequestParam String setName,
            @RequestParam String cardName,
            @RequestParam(required = false) String rarity) {

        String url = cardmarketUrlBuilder.buildSinglesUrl(game, setName, cardName, rarity);
        CardmarketPriceData prices = scrapingService.fetchPrices(url);

        return ResponseEntity.ok(prices);
    }
}
