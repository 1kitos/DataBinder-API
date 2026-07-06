package com.databinder.core.services;


import com.databinder.core.dto.CardResponse;
import com.databinder.core.dto.request.CardCreateRequest;
import com.databinder.core.entities.Card;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

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

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getOracleText()
        );
    }
}
