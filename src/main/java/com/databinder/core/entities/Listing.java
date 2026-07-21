package com.databinder.core.entities;

import java.math.BigDecimal;

import com.databinder.core.enums.Condition;
import com.databinder.core.enums.Country;
import com.databinder.core.enums.Language;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
class Listing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
    private Language language;

    private BigDecimal price;

    private BigDecimal shipping;
    
    private String sellerName;

    @Enumerated(EnumType.STRING)
    private Country sellerCountry;

    @Enumerated(EnumType.STRING)
    private Condition condition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_item_id", nullable = false)
    private WatchlistItem watchlistItem;
    
}
