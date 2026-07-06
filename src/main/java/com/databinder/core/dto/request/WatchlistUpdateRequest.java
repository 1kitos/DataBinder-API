package com.databinder.core.dto.request;

import com.databinder.core.enums.ScrapeFrequency;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchlistUpdateRequest
{
	private Long Id;
    private String name;
    private ScrapeFrequency frequency;
    private Boolean  autoScrapeEnabled;
	
}
