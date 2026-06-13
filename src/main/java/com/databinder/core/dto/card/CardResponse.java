package com.databinder.core.dto.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String name;
    private String oracleText;
}
