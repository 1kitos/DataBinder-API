package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.databinder.core.entities.Listing;
import com.databinder.core.enums.AlertType;
import com.databinder.core.enums.Language;
import com.databinder.scrapping.dtos.ListingFilters;

@Getter
@AllArgsConstructor
public class WatchlistItemResponse {
    private Long id;
    private Long printingId;
    private Instant addedAt;
    private List<AlertType> alerts;

    private Boolean alertEnabled;

    private Boolean alertTriggered;
     
    private ListingFilters filters;
    
    private Map<Language, List<Listing>> listings;

}