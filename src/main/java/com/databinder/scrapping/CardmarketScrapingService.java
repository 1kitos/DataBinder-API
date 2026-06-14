package com.databinder.scrapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CardmarketScrapingService {

    @Value("${scraper.base-url}")
    private String scraperBaseUrl;

    ExchangeStrategies strategies = ExchangeStrategies.builder()
    	    .codecs(configurer -> configurer.defaultCodecs()
    	        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
    	    .build();

    	private final WebClient webClient = WebClient.builder()
    	    .exchangeStrategies(strategies)
    	    .build();
    	
    private final CardmarketPriceParser priceParser;

    public CardmarketPriceData fetchPrices(String url) {
        String html = webClient.get()
        	    .uri(scraperBaseUrl + "/scrape?url=" + url)
        	    .retrieve()
        	    .bodyToMono(String.class)
        	    .block();
        
        System.out.println(html.contains("Price Trend"));
        System.out.println(html.contains("From"));
        
        return priceParser.parse(html);
    }
}
