package com.databinder.scrapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/scraping")
@RequiredArgsConstructor
@Tag(name = "Scraping", description = "Scraping Controller")
public class CardmarketScrapingController {
	
	private final CardmarketScrapingService scrapingService;
	private final CardmarketUrlBuilder cardmarketUrlBuilder;

	@GetMapping("/scrape")
	public ResponseEntity<CardmarketPriceData> scrapePrice(
	        @RequestParam String game,
	        @RequestParam String setName,
	        @RequestParam String cardName) {

	    String url = cardmarketUrlBuilder.buildSinglesUrl(game, setName, cardName);
	    CardmarketPriceData prices = scrapingService.fetchPrices(url);
	    return ResponseEntity.ok(prices);
	}

}
