package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

import com.databinder.core.enums.ScrapeFrequency;

@Getter
@AllArgsConstructor
public class WatchlistResponse {
    private Long id;
    private Long userId;
    private String name;
    private Instant createdAt;
    private ScrapeFrequency scrapeFrequency;
    private Boolean autoScrapeEnabled;
    private List<WatchlistItemResponse> items;
}