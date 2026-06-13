package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Printing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String collectorNumber;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private CardSet cardSet;

    @OneToMany(mappedBy = "printing", cascade = CascadeType.ALL)
    private List<PriceSnapshot> priceSnapshots;
}