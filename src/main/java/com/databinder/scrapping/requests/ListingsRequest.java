package com.databinder.scrapping.requests;

import com.databinder.scrapping.dtos.ListingFilters;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ListingsRequest(
		@JsonProperty("printing_url")
	    String printingUrl,
	    ListingFilters filters
	) {}
