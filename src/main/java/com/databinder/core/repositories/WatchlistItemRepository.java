package com.databinder.core.repositories;

import com.databinder.core.entities.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {
	
    List<WatchlistItem> findByWatchlistId(Long watchlistId);
    boolean existsByWatchlistIdAndPrintingId(Long watchlistId, Long printingId);
}