package com.databinder.core.dto.request;

import java.util.List;

import com.databinder.core.enums.AlertType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateWatchlistItemRequest {

    private List<AlertType> alarmsToAdd;
    private List<AlertType> alarmsToRemove;
    private Boolean alertEnabled;
}