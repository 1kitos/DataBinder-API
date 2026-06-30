package com.databinder.core.entities;


import com.databinder.core.entities.CardSet.Game;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rarities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // "Ultra Rare"
    private String code;
    private String slug;       // "Ultra-Rare"
    private Integer sortOrder; // 1 = most common, higher = scarcer
    
    @Enumerated(EnumType.STRING)
    private Game game;         // rarities are game-specific
}
