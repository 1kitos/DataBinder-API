package com.databinder.core.controller;

import com.databinder.core.dto.PriceSnapshotResponse;
import com.databinder.core.dto.WatchlistItemDetailsResponse;
import com.databinder.core.dto.WatchlistItemResponse;
import com.databinder.core.dto.WatchlistResponse;
import com.databinder.core.dto.request.UpdateWatchlistItemRequest;
import com.databinder.core.dto.request.WatchlistCreateRequest;
import com.databinder.core.dto.request.WatchlistItemCreateRequest;
import com.databinder.core.dto.request.WatchlistUpdateRequest;
import com.databinder.core.enums.AlertType;
import com.databinder.core.services.WatchlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
@RequiredArgsConstructor
@Tag(name = "Watchlists", description = "Manage user watchlists")
public class WatchlistController {

    private final WatchlistService watchlistService;
    
    

    @PostMapping
    public WatchlistResponse create(@Valid @RequestBody WatchlistCreateRequest request) {
        return watchlistService.create(request);
    }

    @GetMapping("/{id}")
    public WatchlistResponse getById(@PathVariable Long id) {
        return watchlistService.getById(id);
    }

    @GetMapping
    public List<WatchlistResponse> getMyWatchlists() {
        return watchlistService.getMyWatchlists();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        watchlistService.delete(id);
    }

    @PostMapping("/{id}/items")
    public WatchlistItemResponse addItem(@PathVariable Long id, @Valid @RequestBody WatchlistItemCreateRequest request) {
        return watchlistService.addItem(id, request);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public void removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        watchlistService.removeItem(id, itemId);
    }
    
    @PatchMapping()
    public WatchlistResponse update(@Valid @RequestBody WatchlistUpdateRequest request)
    {
    	return watchlistService.update(request);
    }
    
    @GetMapping("/watchlist-item/{id}")
    public WatchlistItemResponse getWatchlistItem(@PathVariable Long id) {
        return watchlistService.getItem(id);
    }
    
    @PatchMapping("/watchlist-item/{id}")
    public WatchlistItemResponse updateWatchlistItem(
            @PathVariable Long id,
            @RequestBody UpdateWatchlistItemRequest request) {

        return watchlistService.updateItem(
                id,
                request.getAlarmsToAdd(),
                request.getAlarmsToRemove(),
                request.getAlertEnabled(),
                request.getFilters());
    }
    
    @GetMapping("/watchlist-item/{id}/details")
    public List<WatchlistItemDetailsResponse> getWatchlistItemDetails(@PathVariable Long id)
    {
    	
    	return watchlistService.getItemDetailsForWatchlist(id);
    }
    
    @GetMapping("/watchlist-item/{itemId}/detail")
    public WatchlistItemDetailsResponse getWatchlistItemDetail(
            @PathVariable Long itemId) {
        return watchlistService.getItemDetails(itemId);
    }
    
    @PostMapping("/watchlist-item/{itemId}/fetch")
    public WatchlistItemResponse fetchListengsAndSave(@PathVariable Long itemId) {
        return watchlistService.fetchListingMapForItem(itemId);
    }
    
}