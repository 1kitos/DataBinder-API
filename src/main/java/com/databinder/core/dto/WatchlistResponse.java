package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class WatchlistResponse {
    private Long id;
    private Long userId;
    private String name;
    private Instant createdAt;
    private List<WatchlistItemResponse> items;
}