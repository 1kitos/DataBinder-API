package com.databinder.scrapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record CardmarketPriceData(
    @JsonProperty("from_price") String fromPriceRaw,
    @JsonProperty("price_trend") String priceTrendRaw
) {
    public BigDecimal fromPrice() {
        return parsePrice(fromPriceRaw);
    }

    public BigDecimal priceTrend() {
        return parsePrice(priceTrendRaw);
    }

    private static BigDecimal parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String cleaned = raw
            .replace("€", "")
            .replace("\u00A0", "")
            .replace(",", ".")
            .replaceAll("[^0-9.]", "")
            .trim();
        try {
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return null;
        }
    }
}