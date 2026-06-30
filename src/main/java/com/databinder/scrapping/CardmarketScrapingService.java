package com.databinder.scrapping;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final WebClient webClient;
    private final CardmarketPriceParser priceParser;

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
        String html = webClient.get()
            .uri(scraperBaseUrl + "/scrape?url=" + url)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return priceParser.parse(html);
    }
}
