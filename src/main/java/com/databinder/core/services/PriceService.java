package com.databinder.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.databinder.core.dto.price.PriceSnapshotResponse;
import com.databinder.core.dto.request.PriceSnapshotCreateRequest;
import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.Printing;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.PriceSnapshotRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.scrapping.CardmarketPriceData;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.CardmarketUrlBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceSnapshotRepository priceSnapshotRepository;
    private final PrintingRepository printingRepository;
    private final CardmarketUrlBuilder cardmarketUrlBuilder;
    private final CardmarketScrapingService cardmarketScrapingService;

    public PriceSnapshotResponse create(PriceSnapshotCreateRequest request) {
        Printing printing = printingRepository.findById(request.getPrintingId())
                .orElseThrow(() -> new ResourceNotFoundException("Printing not found: " + request.getPrintingId()));

        PriceSnapshot snapshot = new PriceSnapshot();
        snapshot.setPrinting(printing);
        snapshot.setFromPrice(request.getFromPrice());
        snapshot.setPriceTrend(request.getPriceTrend());
        snapshot.setCurrency(request.getCurrency());
        snapshot.setTimestamp(Instant.now());

        return toResponse(priceSnapshotRepository.save(snapshot));
    }

    public PriceSnapshotResponse getById(Long id) {
        PriceSnapshot snapshot = priceSnapshotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PriceSnapshot not found: " + id));
        return toResponse(snapshot);
    }

    public List<PriceSnapshotResponse> getAll() {
        return priceSnapshotRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PriceSnapshotResponse> getByPrinting(Long printingId) {
        return priceSnapshotRepository.findByPrintingIdOrderByTimestampDesc(printingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    
    public PriceSnapshotResponse getSnapshotForCard(String game, String set, String cardName)
    {
    	PriceSnapshot result = new PriceSnapshot(); 
    	
    	String cmUrl = cardmarketUrlBuilder.buildSinglesUrl(game, set, cardName);
    	CardmarketPriceData priceData = cardmarketScrapingService.fetchPrices(cmUrl);
    	
    	result.setFromPrice(priceData.fromPrice());
    	result.setPriceTrend(priceData.priceTrend());
    	result.setCurrency("EUR");
    	result.setTimestamp(Instant.now());
    	
    	return toResponse(result);
    }
    
    

    public void delete(Long id) {
        priceSnapshotRepository.deleteById(id);
    }

    private PriceSnapshotResponse toResponse(PriceSnapshot snapshot) {

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
}