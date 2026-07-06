package com.databinder.core.services;

import com.databinder.core.dto.WatchlistItemResponse;
import com.databinder.core.dto.WatchlistResponse;
import com.databinder.core.dto.request.WatchlistCreateRequest;
import com.databinder.core.dto.request.WatchlistItemCreateRequest;
import com.databinder.core.dto.request.WatchlistUpdateRequest;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.User;
import com.databinder.core.entities.Watchlist;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.UserRepository;
import com.databinder.core.repositories.WatchlistItemRepository;
import com.databinder.core.repositories.WatchlistRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final UserRepository userRepository;
    private final PrintingRepository printingRepository;

    public WatchlistResponse create(WatchlistCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setName(request.getName());

        return toResponse(watchlistRepository.save(watchlist));
    }

    public WatchlistResponse getById(Long id) {
        return toResponse(findWatchlistOrThrow(id));
    }

    public List<WatchlistResponse> getByUser(Long userId) {
        return watchlistRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        watchlistRepository.deleteById(id);
    }

    public WatchlistResponse addItem(Long watchlistId, WatchlistItemCreateRequest request) {
        Watchlist watchlist = findWatchlistOrThrow(watchlistId);

        Printing printing = printingRepository.findById(request.getPrintingId())
                .orElseThrow(() -> new ResourceNotFoundException("Printing not found: " + request.getPrintingId()));

        boolean alreadyExists = watchlistItemRepository
                .existsByWatchlistIdAndPrintingId(watchlistId, request.getPrintingId());

        if (alreadyExists) {
            throw new IllegalStateException("Printing já está nesta watchlist");
        }

        WatchlistItem item = new WatchlistItem();
        item.setWatchlist(watchlist);
        item.setPrinting(printing);

        watchlistItemRepository.save(item);

        return toResponse(findWatchlistOrThrow(watchlistId));
    }

    public void removeItem(Long watchlistId, Long itemId) {
        WatchlistItem item = watchlistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("WatchlistItem not found: " + itemId));

        if (!item.getWatchlist().getId().equals(watchlistId)) {
            throw new IllegalArgumentException("Item não pertence a esta watchlist");
        }

        watchlistItemRepository.deleteById(itemId);
    }

    private Watchlist findWatchlistOrThrow(Long id) {
        return watchlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found: " + id));
    }
    
    public WatchlistResponse update(WatchlistUpdateRequest request)
    {
    	Watchlist watchlist = findWatchlistOrThrow(request.getId());
    	
    	if(!request.getName().isBlank() && request.getName() != null)
    	{
    		watchlist.setName(request.getName());
    	}
    	
        if (request.getFrequency() != null) {
            watchlist.setScrapeFrequency(request.getFrequency());
        }
        
        if(request.getAutoScrapeEnabled() != null)
        {
        	 watchlist.setAutoScrapeEnabled(request.getAutoScrapeEnabled());
        }
        
        return toResponse(watchlistRepository.save(watchlist));
    }

    private WatchlistResponse toResponse(Watchlist watchlist) {
        List<WatchlistItemResponse> items = watchlist.getItems().stream()
                .map(item -> new WatchlistItemResponse(
                        item.getId(),
                        item.getPrinting().getId(),
                        item.getAddedAt()
                ))
                .toList();

        return new WatchlistResponse(
                watchlist.getId(),
                watchlist.getUser().getId(),
                watchlist.getName(),
                watchlist.getCreatedAt(),
                items
        );
    }
}