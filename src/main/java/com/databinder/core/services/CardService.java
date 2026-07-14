package com.databinder.core.services;


import com.databinder.core.dto.CardDetailsResponse;
import com.databinder.core.dto.CardResponse;
import com.databinder.core.dto.PagedResponse;
import com.databinder.core.dto.PrintingResponse;
import com.databinder.core.dto.request.CardCreateRequest;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.Rarity;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.RarityRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final RarityRepository rarityRepository;

    public CardResponse create(CardCreateRequest request) {
        Card card = new Card();
        card.setName(request.getName());
        card.setOracleText(request.getOracleText());

        return toResponse(cardRepository.save(card));
    }

    public CardResponse getById(Long id) {
        Card card = cardRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Card not found: " + id));

        return toResponse(card);
    }

    public List<CardResponse> getAll() {
        return cardRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CardResponse update(Long id, CardCreateRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setName(request.getName());
        card.setOracleText(request.getOracleText());

        return toResponse(cardRepository.save(card));
    }

    public void delete(Long id) {
        cardRepository.deleteById(id);
    }
    
    public CardDetailsResponse getCardDetails(Long cardId, int page, int pageSize) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found: " + cardId));

        Game game = card.getPrintings().isEmpty()
                ? null
                : card.getPrintings().get(0).getCardSet().getGame();

        Map<String, Integer> raritySortMap = rarityRepository.findByGameOrderBySortOrderAsc(game).stream()
                .collect(Collectors.toMap(Rarity::getName, Rarity::getSortOrder));

        List<PrintingResponse> allPrintings = card.getPrintings()
                .stream()
                .map(this::toPrintingResponse)
                .sorted(
                    Comparator.comparing(PrintingResponse::getSetId)
                              .thenComparing(p -> raritySortMap.getOrDefault(p.getRarity(), Integer.MAX_VALUE))
                )
                .toList();

        int totalItems = allPrintings.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        int springPage = (page > 0) ? page - 1 : 0;
        int fromIndex = Math.min(springPage * pageSize, totalItems);
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<PrintingResponse> pagedPrintings = allPrintings.subList(fromIndex, toIndex);

        PagedResponse<PrintingResponse> printingsPage = new PagedResponse<>(
                pagedPrintings,
                totalItems,
                totalPages,
                page,
                pageSize
        );

        return new CardDetailsResponse(
                card.getId(),
                card.getName(),
                card.getOracleText(),
                printingsPage
        );
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getOracleText()
        );
    }
    
    private PrintingResponse toPrintingResponse(Printing printing) {
        return new PrintingResponse(
                printing.getId(),
                printing.getCard().getId(),
                printing.getCardSet().getId(),
                printing.getCardSet().getName(),
                printing.getCardSet().getCode(),
                printing.getCollectorNumber(),
                printing.getImageUrl(),
                printing.getRarity(),
                printing.getIsPromo()
        );
    }
}
