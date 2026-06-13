package com.databinder.core.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PriceSnapshotCreateRequest {

    @NotNull
    private Long printingId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;

    @NotBlank
    private String currency;
}
