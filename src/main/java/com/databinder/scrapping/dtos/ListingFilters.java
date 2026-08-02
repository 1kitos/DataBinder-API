package com.databinder.scrapping.dtos;

import java.util.List;

import com.databinder.core.enums.Condition;
import com.databinder.core.enums.Country;
import com.databinder.core.enums.Language;
import com.databinder.core.enums.SellerType;

public record ListingFilters(
        List<String> languages,
        List<String> excluded_countries,
        List<String> seller_types,
        String min_condition,
        Boolean is_signed,
        Boolean is_first_edition,
        Boolean is_altered,
        Integer max_listings
) {

    public ListingFilters() {
        this(
            List.of(),
            List.of(),
            List.of(),
            null,
            false,
            false,
            false,
            10
        );
    }
    
    
    public ListingFilters normalize() {
        return new ListingFilters(
                languages != null ? languages : List.of(),
                excluded_countries != null ? excluded_countries : List.of(),
                seller_types != null ? seller_types : List.of(),
                min_condition,
                is_signed != null ? is_signed : false,
                is_first_edition != null ? is_first_edition : false,
                is_altered != null ? is_altered : false,
                max_listings != null ? max_listings : 10
        );
    }
    
}