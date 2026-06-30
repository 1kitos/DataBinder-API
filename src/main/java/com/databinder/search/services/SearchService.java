package com.databinder.search.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.databinder.core.dto.card.CardResponse;
import com.databinder.core.dto.printing.PrintingResponse;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CardRepository cardRepository;
    private final PrintingRepository printingRepository;

//    public List<CardResponse> searchCards(Game game, String query, int page, int pageSize) {
//
//        Pageable pageable = PageRequest.of(page, pageSize);
//
//        Page<Card> resultPage =
//                cardRepository.findByGameAndNameContainingIgnoreCase(
//                        game,
//                        query,
//                        pageable
//                );
//
//        return resultPage
//                .stream()
//                .map(this::toResponse)
//                .toList();
//    }
    
    
    public List<CardResponse> searchCards(Game game, String query, int page, int pageSize) {
        // If your client API expects 1 to be the first page, use: page - 1
        // If your client API already sends 0 for the first page, leave it as page
        int springPage = (page > 0) ? page - 1 : 0; 
        
        Pageable pageable = PageRequest.of(springPage, pageSize);
        
        return cardRepository
                .searchRanked(game.name(), query, pageable)
                .stream()
                .map(this::toResponse)
                .toList();
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
                .map(this::toResponse)
                .toList();
    }
    

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getOracleText()
        );
    }
    
    private PrintingResponse toResponse(Printing printing) {
        return new PrintingResponse(
                printing.getId(),
                printing.getCard().getId(),
                printing.getCardSet().getId(),
                printing.getCollectorNumber(),
                printing.getImageUrl(),
                printing.getRarity(),
                printing.getIsPromo()
        );
    }
    
}