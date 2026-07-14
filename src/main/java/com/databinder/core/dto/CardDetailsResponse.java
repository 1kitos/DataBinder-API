package com.databinder.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardDetailsResponse {
    private Long id;
    private String name;
    private String oracleText;
    private PagedResponse<PrintingResponse> printings;
}
