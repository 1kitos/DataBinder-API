package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.databinder.core.enums.AlertType;
import com.databinder.core.enums.Language;
import com.databinder.scrapping.dtos.ListingFilters;

@Entity
@Table(name = "watchlist_items")
@Getter
@Setter
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "watchlist_id")
    private Watchlist watchlist;

    @ManyToOne(optional = false)
    @JoinColumn(name = "printing_id")
    private Printing printing;

    @Column(nullable = false)
    private boolean autoScrapeEnabled = true;

    private Instant addedAt;
    
    private List<AlertType> alerts;

    private Boolean alertEnabled;

    private Boolean alertTriggered;
    
    @Transient
    private ListingFilters filters;
    
    @Transient
    private Map<Language, List<Listing>> listings = new HashMap<Language, List<Listing>>();

    @PrePersist
    protected void onCreate() {
        this.addedAt = Instant.now();
    }
}