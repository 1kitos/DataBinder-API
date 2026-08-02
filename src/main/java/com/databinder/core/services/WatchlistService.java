package com.databinder.core.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.databinder.core.dto.PriceSnapshotResponse;
import com.databinder.core.dto.WatchlistItemDetailsResponse;
import com.databinder.core.dto.WatchlistItemResponse;
import com.databinder.core.dto.WatchlistResponse;
import com.databinder.core.dto.request.WatchlistCreateRequest;
import com.databinder.core.dto.request.WatchlistItemCreateRequest;
import com.databinder.core.dto.request.WatchlistUpdateRequest;
import com.databinder.core.entities.Listing;
import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.User;
import com.databinder.core.entities.Watchlist;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.enums.AlertType;
import com.databinder.core.enums.Language;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.mapping.ScrapperEntityMapper;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.WatchlistItemRepository;
import com.databinder.core.repositories.WatchlistRepository;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.dtos.ListingFilters;
import com.databinder.scrapping.requests.ListingsRequest;
import com.databinder.scrapping.responses.CardmarketListingData;
import com.databinder.scrapping.responses.CardmarketPriceData;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final PrintingRepository printingRepository;
    private final AuthenticationService authenticationService;
    private final CardmarketScrapingService cardmarketScrapingService;

    public WatchlistResponse create(WatchlistCreateRequest request) {

        User currentUser = authenticationService.getCurrentUser();

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(currentUser);
        watchlist.setName(request.getName());

        return ResponseMapper.toResponse(
                watchlistRepository.save(watchlist));
    }

    public List<WatchlistResponse> getMyWatchlists() {

        User currentUser = authenticationService.getCurrentUser();

        return watchlistRepository.findByUserId(currentUser.getId())
                .stream()
                .map(ResponseMapper::toResponse)
                .toList();
    }

    public WatchlistResponse getById(Long id) {
        return ResponseMapper.toResponse(findOwnedWatchlist(id));
    }

    public void delete(Long id) {

        Watchlist watchlist = findOwnedWatchlist(id);

        watchlistRepository.delete(watchlist);
    }

    public WatchlistResponse update(WatchlistUpdateRequest request) {

        Watchlist watchlist = findOwnedWatchlist(request.getId());

        if (request.getName() != null && !request.getName().isBlank()) {
            watchlist.setName(request.getName());
        }

        if (request.getFrequency() != null) {
            watchlist.setScrapeFrequency(request.getFrequency());
        }

        if (request.getAutoScrapeEnabled() != null) {
            watchlist.setAutoScrapeEnabled(request.getAutoScrapeEnabled());
        }

        return ResponseMapper.toResponse(
                watchlistRepository.save(watchlist));
    }

    public WatchlistItemResponse addItem(
            Long watchlistId,
            WatchlistItemCreateRequest request) {

        Watchlist watchlist = findOwnedWatchlist(watchlistId);

        Printing printing = printingRepository.findById(request.getPrintingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Printing not found: " + request.getPrintingId()));

        if (watchlistItemRepository.existsByWatchlistIdAndPrintingId(
                watchlistId,
                request.getPrintingId())) {
            throw new IllegalStateException(
                    "Printing already exists in this watchlist.");
        }

        ListingFilters filters = request.getFilters() != null
                ? request.getFilters()
                : new ListingFilters();

        WatchlistItem item = new WatchlistItem();
        item.setWatchlist(watchlist);
        item.setPrinting(printing);
        item.setFilters(filters.normalize());

        // Persist first so the item exists
        watchlistItemRepository.save(item);

        // Fetch latest price
        CardmarketPriceData priceData =
                cardmarketScrapingService.fetchPrices(printing.getPrintingUrl());

        PriceSnapshot snapshot =
                ScrapperEntityMapper.toPriceSnapshot(priceData, printing);

        printing.addPriceSnapshot(snapshot);
        printingRepository.save(printing);

        // Reuse the exact logic that already works
        fetchListingMapForItem(item.getId());

        return ResponseMapper.toResponse(item);
    }
    
    public void removeItem(Long watchlistId, Long itemId) {

        WatchlistItem item = findOwnedWatchlistItem(itemId);

        if (!item.getWatchlist().getId().equals(watchlistId)) {
            throw new IllegalArgumentException(
                    "Item does not belong to this watchlist.");
        }

        watchlistItemRepository.delete(item);
    }

    public WatchlistItemResponse getItem(Long id) {

        WatchlistItem item = findOwnedWatchlistItem(id);

        return ResponseMapper.toResponse(item);
    }

    public WatchlistItemResponse updateItem(
            Long id,
            List<AlertType> alarmsToAdd,
            List<AlertType> alarmsToRemove,
            Boolean alertEnabled,
            ListingFilters filters) {

        WatchlistItem item = findOwnedWatchlistItem(id);

        List<AlertType> alerts =
                item.getAlerts() != null
                        ? new ArrayList<>(item.getAlerts())
                        : new ArrayList<>();

        if (alarmsToAdd != null) {
            for (AlertType alert : alarmsToAdd) {
                if (!alerts.contains(alert)) {
                    alerts.add(alert);
                }
            }
        }

        if (alarmsToRemove != null) {
            alerts.removeAll(alarmsToRemove);
        }

        item.setAlerts(alerts);
        
        item.setFilters(filters);

        if (alertEnabled != null) {
            item.setAlertEnabled(alertEnabled);
        }

        return ResponseMapper.toResponse(
                watchlistItemRepository.save(item));
    }

    public List<WatchlistItemDetailsResponse> getItemDetailsForWatchlist(Long id) {

        Watchlist watchlist = findOwnedWatchlist(id);

        return watchlist.getItems()
                .stream()
                .map(ResponseMapper::toDetailsResponse)
                .toList();
    }

    private Watchlist findOwnedWatchlist(Long id) {

        User currentUser = authenticationService.getCurrentUser();

        Watchlist watchlist = watchlistRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Watchlist not found: " + id));

        if (!watchlist.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You do not own this watchlist.");
        }

        return watchlist;
    }

    private WatchlistItem findOwnedWatchlistItem(Long id) {

        User currentUser = authenticationService.getCurrentUser();

        WatchlistItem item = watchlistItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Watchlist item not found: " + id));

        if (!item.getWatchlist().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You do not own this watchlist item.");
        }

        return item;
    }
    
    public WatchlistItemResponse fetchListingMapForItem(Long itemId)
    {
        User currentUser = authenticationService.getCurrentUser();

        WatchlistItem item = watchlistItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Watchlist item not found: " + itemId));

        if (!item.getWatchlist().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You do not own this watchlist item.");
        }
        
        ListingsRequest request = new ListingsRequest(item.getPrinting().getPrintingUrl(), item.getFilters());

        
        Map<String, List<CardmarketListingData>> listingsMap = cardmarketScrapingService.fetchListings(request);
        
        item.setListings(ScrapperEntityMapper.toListingMap(listingsMap));
        
        return ResponseMapper.toResponse(watchlistItemRepository.save(item));
    }
    
    
    public WatchlistItemDetailsResponse getItemDetails(Long id) {

        WatchlistItem item = findOwnedWatchlistItem(id);

        return ResponseMapper.toDetailsResponse(item);
    }
    
    
    @Transactional
    public WatchlistItem refreshListings(Long itemId) {

        WatchlistItem item = watchlistItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Watchlist item not found: " + itemId));

        ListingsRequest request = new ListingsRequest(
                item.getPrinting().getPrintingUrl(),
                item.getFilters()
        );

        Map<String, List<CardmarketListingData>> listingsMap =
                cardmarketScrapingService.fetchListings(request);

        item.setListings(
                ScrapperEntityMapper.toListingMap(listingsMap)
        );

        return watchlistItemRepository.save(item);
    }
        
    
    
    
    
    
    
   
    
    
}