package com.databinder.scrapping.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.services.PrintingService;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.CardmarketUrlBuilder;
import com.databinder.scrapping.dtos.ListingFilters;
import com.databinder.scrapping.requests.ListingsRequest;
import com.databinder.scrapping.responses.CardmarketListingData;
import com.databinder.scrapping.responses.CardmarketPriceData;
import com.databinder.scrapping.responses.CardmarketVersionData;
import com.databinder.search.services.SearchService;

import org.springframework.web.bind.annotation.RequestBody;

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
    
    @GetMapping("/versions/{id}")
    public ResponseEntity<List<CardmarketVersionData>> getVersions(
            @RequestParam Game game,
            @PathVariable Long id) {

        String url = cardmarketUrlBuilder.buildVersionsUrl(game, id);
        List<CardmarketVersionData> versions = scrapingService.fetchVersions(url);

        return ResponseEntity.ok(versions);
    }
    
    @PostMapping("/listings")
    public ResponseEntity<Map<String, List<CardmarketListingData>>> getListings(
            @RequestBody ListingsRequest request) {

        return ResponseEntity.ok(
            scrapingService.fetchListings(request)
        );
    }
    
    
}
