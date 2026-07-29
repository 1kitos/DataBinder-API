package com.databinder.scrapping.responses;

import com.databinder.core.enums.Condition;
import com.databinder.core.enums.Country;
import com.databinder.core.enums.Language;
import com.databinder.core.enums.SellerType;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.databinder.scrapping.mapping.CountryMapper;
import com.databinder.scrapping.mapping.LanguageMapper;
import com.databinder.scrapping.mapping.ConditionMapper;

import java.math.BigDecimal;

public record CardmarketListingData(

        @JsonProperty("seller")
        String sellerName,

        @JsonProperty("country")
        String sellerCountryRaw,

        @JsonProperty("professional")
        Boolean professional,

        @JsonProperty("sales")
        Integer sales,

        @JsonProperty("available_items")
        Integer availableItems,

        @JsonProperty("condition")
        String conditionRaw,

        @JsonProperty("language")
        String language,
        
        @JsonProperty("first_edition")
        Boolean firstEdition,

        @JsonProperty("quantity")
        Integer quantity,

        @JsonProperty("price")
        String priceRaw

) {

    public BigDecimal price() {
        return parsePrice(priceRaw);
    }

    public Country sellerCountry() {
        return CountryMapper.fromCardmarket(sellerCountryRaw);
    }

    public Condition condition() {
        return ConditionMapper.fromCardmarket(conditionRaw);
    }

    public SellerType sellerType() {
        if (professional == null) {
            return null;
        }

        return professional
                ? SellerType.PROFESSIONAL
                : SellerType.PRIVATE;
    }

    private static BigDecimal parsePrice(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

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