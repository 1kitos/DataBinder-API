package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

import com.databinder.core.enums.AlertType;

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

    @PrePersist
    protected void onCreate() {
        this.addedAt = Instant.now();
    }
}