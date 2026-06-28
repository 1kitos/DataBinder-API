package com.databinder.core.dto.printing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PrintingResponse {

    private Long id;
    private Long cardId;
    private Long setId;
    private String collectorNumber;
    private String imageUrl;
    private String rarity;
}