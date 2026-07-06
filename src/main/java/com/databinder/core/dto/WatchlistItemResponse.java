package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

import com.databinder.core.enums.AlertType;

@Getter
@AllArgsConstructor
public class WatchlistItemResponse {
    private Long id;
    private Long printingId;
    private Instant addedAt;
    private List<AlertType> alerts;

    private Boolean alertEnabled;

    private Boolean alertTriggered;

}