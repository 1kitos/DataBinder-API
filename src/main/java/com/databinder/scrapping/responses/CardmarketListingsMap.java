package com.databinder.scrapping.responses;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardmarketListingsMap {
	Map<String, List<CardmarketListingData>> listings;
}
