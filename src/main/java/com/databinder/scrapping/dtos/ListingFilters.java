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
	) {}