package com.databinder.core.dto;

import java.util.List;

import com.databinder.core.enums.AlertType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistItemDetailsResponse {

    private Long id;

    private PrintingDetailsResponse printing;

    private Boolean alertEnabled;

    private Boolean alertTriggered;

    private List<AlertType> alerts;

    // mais tarde
    // private List<PriceSnapshotResponse> snapshots;
    // private List<MessageResponse> messages;
}
