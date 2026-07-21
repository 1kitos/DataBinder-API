package com.databinder.scrapping;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import com.databinder.scrapping.dtos.ListingFilters;
import com.databinder.scrapping.parsers.CardmarketPriceParser;
import com.databinder.scrapping.requests.ListingsRequest;
import com.databinder.scrapping.responses.CardmarketListingData;
import com.databinder.scrapping.responses.CardmarketPriceData;
import com.databinder.scrapping.responses.CardmarketVersionData;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CardmarketScrapingService {

    @Value("${scraper.base-url}")
    private String scraperBaseUrl;

    private final WebClient webClient;
    private final CardmarketPriceParser priceParser;
    
    @PostConstruct
    public void debug() {
        System.out.println("SCRAPER URL = [" + scraperBaseUrl + "]");
    }

    @Autowired
    public CardmarketScrapingService(CardmarketPriceParser priceParser) {
        this.priceParser = priceParser;
        this.webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build())
            .build();
    }

    public CardmarketPriceData fetchPrices(String url) {
        return webClient.get()
            .uri(scraperBaseUrl + "/scrape?url=" + url)
            .retrieve()
            .bodyToMono(CardmarketPriceData.class)
            .block();
    }
    
    public List<CardmarketVersionData> fetchVersions(String url) {
        return webClient.get()
                .uri(scraperBaseUrl + "/versions?url=" + url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CardmarketVersionData>>() {})
                .block();
    }
    
    public Map<String, List<CardmarketListingData>> fetchListings(
            ListingsRequest request) {

        return webClient.post()
                .uri(scraperBaseUrl + "/listings")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<
                        Map<String, List<CardmarketListingData>>>() {})
                .block();
    }
}
