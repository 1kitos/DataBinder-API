package com.databinder.core.repositories;

import com.databinder.core.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserId(Long userId);
    List<Watchlist> findByAutoScrapeEnabledTrue();
}