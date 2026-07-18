package com.databinder.core.mapping;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.databinder.core.dto.CardResponse;
import com.databinder.core.dto.MessageResponse;
import com.databinder.core.dto.PriceSnapshotResponse;
import com.databinder.core.dto.PrintingDetailsResponse;
import com.databinder.core.dto.PrintingResponse;
import com.databinder.core.dto.RarityResponse;
import com.databinder.core.dto.SetResponse;
import com.databinder.core.dto.UserResponse;
import com.databinder.core.dto.WatchlistItemDetailsResponse;
import com.databinder.core.dto.WatchlistItemResponse;
import com.databinder.core.dto.WatchlistResponse;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet;
import com.databinder.core.entities.Message;
import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.Rarity;
import com.databinder.core.entities.User;
import com.databinder.core.entities.Watchlist;
import com.databinder.core.entities.WatchlistItem;

public  class ResponseMapper 
{

    public static PrintingResponse toResponse(Printing printing) {
        return new PrintingResponse(
                printing.getId(),
                printing.getCard().getId(),
                printing.getCardSet().getId(),
                printing.getCardSet().getName(),
                printing.getCardSet().getCode(),
                printing.getCollectorNumber(),
                printing.getImageData() != null
                ? "/api/printings/" + printing.getId() + "/image"
                : printing.getImageUrl(), // fallback to stored URL if no binary image yet
                printing.getRarity(),
                printing.getIsPromo()
        );
    }
    
    
    public static PrintingDetailsResponse toDetailsResponse(Printing printing) {
        Card printingCard = printing.getCard();
        return new PrintingDetailsResponse(
            printing.getId(),
            printingCard.getName(),
            printing.getCardSet().getName(),
            printing.getCollectorNumber(),
            printing.getRarity(),
            printing.getImageData() != null
                ? "/api/printings/" + printing.getId() + "/image"
                : printing.getImageUrl(), // fallback to stored URL if no binary image yet
            printing.getVersionNumber(),
            printing.getPriceSnapshots().stream()
                .map(ResponseMapper::toResponse) // or however you're mapping now
                .collect(Collectors.toList())
        );
    }
    
    public static PriceSnapshotResponse toResponse(PriceSnapshot snapshot) {

        Long id = snapshot != null ? snapshot.getId() : null;

        Long printingId = (snapshot != null && snapshot.getPrinting() != null)
                ? snapshot.getPrinting().getId()
                : null;

        BigDecimal fromPrice = snapshot != null ? snapshot.getFromPrice() : null;
        BigDecimal priceTrend = snapshot != null ? snapshot.getPriceTrend() : null;
        BigDecimal customPrice = snapshot != null ? snapshot.getCustomPrice() : null;

        String currency = snapshot != null ? snapshot.getCurrency() : null;

        Instant timestamp = snapshot != null ? snapshot.getTimestamp() : null;

        return new PriceSnapshotResponse(
                id,
                printingId,
                fromPrice,
                priceTrend,
                customPrice,
                currency,
                timestamp
        );
    }
	
    public static SetResponse toResponse(CardSet cardSet) {
        return new SetResponse(
                cardSet.getId(),
                cardSet.getName(),
                cardSet.getCode()
        );
    }
    
    public static RarityResponse toResponse(Rarity rarity) {
        return new RarityResponse(
            rarity.getId(),
            rarity.getName(),
            rarity.getCode(),
            rarity.getSlug(),
            rarity.getSortOrder(),
            rarity.getGame()
        );
    }
    
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
    
    public static WatchlistResponse toResponse(Watchlist watchlist) {
        List<WatchlistItemResponse> items = watchlist.getItems().stream()
                .map(ResponseMapper::toResponse)
                .toList();

        return new WatchlistResponse(
                watchlist.getId(),
                watchlist.getUser().getId(),
                watchlist.getName(),
                watchlist.getCreatedAt(),
                watchlist.getScrapeFrequency(),
                watchlist.isAutoScrapeEnabled(),
                items
        );
    }

    public static WatchlistItemResponse toResponse(WatchlistItem watchlistItem) {
        return new WatchlistItemResponse(
                watchlistItem.getId(),
                watchlistItem.getPrinting().getId(),
                watchlistItem.getAddedAt(),
                watchlistItem.getAlerts(),
                watchlistItem.getAlertEnabled(),
                watchlistItem.getAlertTriggered()
        );
    }
    
    public static WatchlistItemDetailsResponse toDetailsResponse(WatchlistItem item)
    {
    	
    	WatchlistItemDetailsResponse result =  new WatchlistItemDetailsResponse();
    	    	
    	result.setId(item.getId());
    	result.setPrinting(ResponseMapper.toDetailsResponse(item.getPrinting()));
    	result.setAlertEnabled(item.getAlertEnabled());
    	result.setAlertTriggered(item.getAlertTriggered());
    	result.setAlerts(item.getAlerts());
    	
    	return result;
    }
    
    public static MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getToUser().getId(),
                message.getHeader(),
                message.getBody(),
                message.getStatus()
        );
    }
    
    public static CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getOracleText()
        );
    }
    
    
	
}
