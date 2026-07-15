package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardSearchResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private int printingCount;
}