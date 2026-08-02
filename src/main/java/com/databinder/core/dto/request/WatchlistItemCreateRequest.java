package com.databinder.core.dto.request;

import com.databinder.scrapping.dtos.ListingFilters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchlistItemCreateRequest {
    private Long printingId;
    private ListingFilters filters;
}