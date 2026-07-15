package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.databinder.core.enums.ScrapeFrequency;

@Entity
@Table(name = "watchlists")
@Getter
@Setter
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean autoScrapeEnabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScrapeFrequency scrapeFrequency = ScrapeFrequency.DAILY;

    private Instant lastScrapedAt;

    private Instant createdAt;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchlistItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}