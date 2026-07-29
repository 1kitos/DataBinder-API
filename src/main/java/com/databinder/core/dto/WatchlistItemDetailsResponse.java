package com.databinder.core.dto;

import java.util.List;
import java.util.Map;

import com.databinder.core.entities.Listing;
import com.databinder.core.enums.AlertType;
import com.databinder.core.enums.Language;
import com.databinder.scrapping.dtos.ListingFilters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistItemDetailsResponse {

    private Long id;

    private PrintingDetailsResponse printing;

    private Boolean alertEnabled;

    private Boolean alertTriggered;

    private List<AlertType> alerts;
    
    private ListingFilters filters;
    
    private Map<Language, List<Listing>> listings;

}
