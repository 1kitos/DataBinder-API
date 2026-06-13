package com.databinder.core.dto.price;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class PriceSnapshotResponse {

    private Long id;
    private Long printingId;
    private BigDecimal price;
    private String currency;
    private Instant timestamp;
}
