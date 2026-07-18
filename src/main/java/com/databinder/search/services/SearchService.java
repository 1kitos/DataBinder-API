package com.databinder.search.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.databinder.core.dto.CardResponse;
import com.databinder.core.dto.CardSearchResponse;
import com.databinder.core.dto.PagedResponse;
import com.databinder.core.dto.PrintingResponse;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.Printing;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CardRepository cardRepository;
    private final PrintingRepository printingRepository;

        
    public PagedResponse<CardResponse> searchCards(Game game, String query, int page, int pageSize) {
        int springPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(springPage, pageSize);

        Page<Card> resultPage = cardRepository.searchRanked(game.name(), query, pageable);

        List<CardResponse> items = resultPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PagedResponse<>(
                items,
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                page,
                pageSize
        );
    }
    
    public List<PrintingResponse> searchPrintingsAdvanced(
            Game game, 
            String cardName, 
            String setCode, 
            String rarity, 
            int page, 
            int pageSize
    ) {
        int springPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(springPage, pageSize);

        return printingRepository
                .searchPrintingsAdvanced(game.name(), cardName, setCode, rarity, pageable)
                .stream()
                .map(ResponseMapper::toResponse)
                .toList();
    }
    
    
    
    public PagedResponse<CardSearchResponse> browseCards(Game game, String query, int page, int pageSize) {
        int springPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(springPage, pageSize);

        Page<Card> resultPage = cardRepository.searchRanked(game.name(), query, pageable);

        List<CardSearchResponse> items = resultPage.getContent()
                .stream()
                .map(this::toSearchResponse)
                .toList();

        return new PagedResponse<>(
                items,
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                page,
                pageSize
        );
    }
    

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getOracleText()
        );
    }
    

    private CardSearchResponse toSearchResponse(Card card) {

        Printing printing = card.getPrintings().isEmpty()
                ? null
                : card.getPrintings().get(0);

        String imageUrl = null;

        if (printing != null) {
            imageUrl = printing.getImageData() != null
                    ? "/api/printings/" + printing.getId() + "/image"
                    : printing.getImageUrl();
        }

        return new CardSearchResponse(
                card.getId(),
                card.getName(),
                imageUrl,
                card.getPrintings().size()
        );
    }
    
}