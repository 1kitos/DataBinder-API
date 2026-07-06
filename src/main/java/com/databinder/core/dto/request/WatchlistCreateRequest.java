package com.databinder.core.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WatchlistCreateRequest {
    private Long userId;
    private String name;
}