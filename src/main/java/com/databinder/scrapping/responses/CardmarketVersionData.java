package com.databinder.scrapping.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardmarketVersionData(
	    @JsonProperty("name") String name,
	    @JsonProperty("set_name") String setName,
	    @JsonProperty("set_code") String setCode,
	    @JsonProperty("printing_url") String printingUrl,
	    @JsonProperty("image_url") String imageUrl,
	    @JsonProperty("image_data") String imageData, // base64
	    @JsonProperty("version_number") Integer versionNumber,
	    @JsonProperty("version") String version
	) {}
