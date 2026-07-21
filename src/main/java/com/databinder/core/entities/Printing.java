package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.databinder.scrapping.responses.CardmarketVersionData;

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
    
    private String printingUrl;

    private String imageUrl;
    
    private String rarity;
    
    private String isPromo;
    
    private Integer versionNumber;
    
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "image_data", columnDefinition = "bytea")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private CardSet cardSet;

    @OneToMany(mappedBy = "printing", cascade = CascadeType.ALL)
    private List<PriceSnapshot> priceSnapshots;
    
    
    public void updateAttributes(CardmarketVersionData data) {
        this.printingUrl = data.printingUrl();
        this.imageUrl = data.imageUrl();
        this.versionNumber = data.versionNumber();
        
        if (data.imageData() != null) {
            this.imageData = Base64.getDecoder().decode(data.imageData());
        }
        
        if (data.version() != null) {
            this.rarity = data.version();
        }
        
    }
    
}