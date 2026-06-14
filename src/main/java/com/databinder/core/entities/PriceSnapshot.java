package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal fromPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceTrend;

    @Column(precision = 10, scale = 2)
    private BigDecimal customPrice; // lógica a implementar depois

    private String currency;

    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "printing_id", nullable = true)
    private Printing printing;
}