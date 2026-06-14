package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sets")
public class CardSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Game game;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private List<Printing> printings;

    public String toCardmarketSlug() {
        return name
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "-");
    }
    
    
    
    
    
    public enum Game {
        YUGIOH("YuGiOh"),
        MAGIC("Magic"),
        POKEMON("Pokemon");

        private final String cardmarketPath;

        Game(String cardmarketPath) {
            this.cardmarketPath = cardmarketPath;
        }

        public String getCardmarketPath() {
            return cardmarketPath;
        }
    }
}