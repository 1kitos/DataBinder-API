package com.databinder.core.controller;

import com.databinder.core.dto.WatchlistResponse;
import com.databinder.core.dto.request.WatchlistCreateRequest;
import com.databinder.core.dto.request.WatchlistItemCreateRequest;
import com.databinder.core.dto.request.WatchlistUpdateRequest;
import com.databinder.core.services.WatchlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watchlists")
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
    public List<WatchlistResponse> getByUser(@RequestParam Long userId) {
        return watchlistService.getByUser(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        watchlistService.delete(id);
    }

    @PostMapping("/{id}/items")
    public WatchlistResponse addItem(@PathVariable Long id, @Valid @RequestBody WatchlistItemCreateRequest request) {
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
}