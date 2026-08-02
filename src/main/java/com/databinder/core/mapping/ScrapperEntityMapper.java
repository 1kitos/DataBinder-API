package com.databinder.core.mapping;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.databinder.core.entities.Listing;
import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.Printing;
import com.databinder.core.enums.Condition;
import com.databinder.core.enums.Country;
import com.databinder.core.enums.Language;
import com.databinder.scrapping.mapping.LanguageMapper;
import com.databinder.scrapping.responses.CardmarketListingData;
import com.databinder.scrapping.responses.CardmarketListingsMap;
import com.databinder.scrapping.responses.CardmarketPriceData;

public class ScrapperEntityMapper {

    public static Map<Language, List<Listing>> toListingMap(Map<String, List<CardmarketListingData>> listingsMap) {

        Map<Language, List<Listing>> result = new HashMap<>();

        for (Map.Entry<String, List<CardmarketListingData>> entry
                : listingsMap.entrySet()) {

            Language language = Language.valueOf(entry.getKey());

            List<Listing> listings = entry.getValue()
                    .stream()
                    .map(ScrapperEntityMapper::toListing)
                    .collect(Collectors.toList());

            result.put(language, listings);
        }

        return result;
    }

    public static Listing toListing(CardmarketListingData data) {

        Listing listing = new Listing();

        listing.setPrice(data.price());
        listing.setSellerName(data.sellerName());
        listing.setProfessional(data.professional());
        listing.setSales(data.sales());
        listing.setAvailableItems(data.availableItems());
        listing.setQuantity(data.quantity());
        listing.setFirstEdition(data.firstEdition());

        listing.setLanguage(LanguageMapper.fromCardmarket(data.language()));
        listing.setSellerCountry(data.sellerCountry());
        listing.setCondition(data.condition());

        return listing;
    }
    
    
    public static PriceSnapshot toPriceSnapshot(CardmarketPriceData data, Printing printing)
    {
    	PriceSnapshot result = new PriceSnapshot();
    	
    	result.setPrinting(printing);
    	result.setFromPrice(data.fromPrice());
    	result.setPriceTrend(data.priceTrend());
    	result.setCurrency("EUR");
    	result.setTimestamp(Instant.now());
    	
    	return result;
    }
    
}