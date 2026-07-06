package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class WatchlistItemResponse {
    private Long id;
    private Long printingId;
    private Instant addedAt;
}