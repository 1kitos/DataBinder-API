package com.databinder.core.services;

import com.databinder.core.dto.PrintingResponse;
import com.databinder.core.dto.WatchlistItemDetailsResponse;
import com.databinder.core.dto.WatchlistItemResponse;
import com.databinder.core.dto.WatchlistResponse;
import com.databinder.core.dto.request.WatchlistCreateRequest;
import com.databinder.core.dto.request.WatchlistItemCreateRequest;
import com.databinder.core.dto.request.WatchlistUpdateRequest;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.User;
import com.databinder.core.entities.Watchlist;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.enums.AlertType;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.UserRepository;
import com.databinder.core.repositories.WatchlistItemRepository;
import com.databinder.core.repositories.WatchlistRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final UserRepository userRepository;
    private final PrintingRepository printingRepository;
    private final PrintingService printingService;

    public WatchlistResponse create(WatchlistCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setName(request.getName());

        return ResponseMapper.toResponse(watchlistRepository.save(watchlist));
    }

    public WatchlistResponse getById(Long id) {
        return ResponseMapper.toResponse(findWatchlistOrThrow(id));
    }

    public List<WatchlistResponse> getByUser(Long userId) {
        return watchlistRepository.findByUserId(userId)
                .stream()
                .map(ResponseMapper::toResponse)
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

        return ResponseMapper.toResponse(findWatchlistOrThrow(watchlistId));
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
        
        return ResponseMapper.toResponse(watchlistRepository.save(watchlist));
    }
    
    
    public WatchlistItemResponse updateItem(Long Id,List<AlertType> alarmsToAdd,
    									 	List<AlertType> alarmsToRemove,Boolean alertEnabled)
    {
    	WatchlistItem item = watchlistItemRepository.findById(Id)
    			.orElseThrow(() -> new ResourceNotFoundException("Watchlist Item not found: " + Id));
    	
    	List<AlertType> alerts = (item.getAlerts() != null) ? item.getAlerts() : new ArrayList<AlertType>();
    	
    	
    	
    	for(AlertType a : alarmsToAdd)
    	{
    		if(alerts.contains(a))
    		{
    			continue;
    		}
    		alerts.add(a);
    	}
    	
    	for (AlertType a : alarmsToRemove)
    	{
    		if(!alerts.contains(a))
    		{
    			continue;
    		}
    		alerts.remove(a);
    	}
    	
    	item.setAlerts(alerts);
    	
    	if(alertEnabled != null)
    	{
    		item.setAlertEnabled(alertEnabled);
    	}
    	
    	return ResponseMapper.toResponse(watchlistItemRepository.save(item));
    }
    
    public WatchlistItemResponse getItem(Long id) {
        WatchlistItem item = watchlistItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist Item not found: " + id));

        return ResponseMapper.toResponse(item);
    }
    
    public List<WatchlistItemDetailsResponse> getItemDetailsForWatchlist(Long id)
    {
    	Watchlist list = watchlistRepository.findById(id)
    			.orElseThrow(() -> new ResourceNotFoundException("Watchlist Item not found: " + id));
    	
    	List<WatchlistItemDetailsResponse> itemDetailsResponse = list.getItems().stream()
    			.map(ResponseMapper::toDetailsResponse)
    			.toList();
    	 	
    	return itemDetailsResponse;
    }

    
    
//    private WatchlistItemDetailsResponse toDetailsResponse(WatchlistItem item) {
//        Printing printing = item.getPrinting();
//
//        PrintingResponse printingResponse = new PrintingResponse(
//                printing.getId(),
//                printing.getCard().getId(),
//                printing.getCardSet().getId(),
//                printing.getCardSet().getName(),
//                printing.getCardSet().getCode(),
//                printing.getCollectorNumber(),
//                printing.getImageUrl(),
//                printing.getRarity(),
//                printing.getIsPromo()
//        );
//
//        return new WatchlistItemDetailsResponse(
//                item.getId(),
//                printingResponse,
//                item.getAlertTriggered(),
//                item.getAlertEnabled(),
//                item.getAlerts()
//        );
//    }
    
//    private Watchlist
}