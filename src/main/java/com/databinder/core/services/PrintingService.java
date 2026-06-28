package com.databinder.core.services;


import com.databinder.core.dto.printing.PrintingResponse;
import com.databinder.core.dto.request.PrintingCreateRequest;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet;
import com.databinder.core.entities.Printing;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.SetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrintingService {

    private final PrintingRepository printingRepository;
    private final CardRepository cardRepository;
    private final SetRepository setRepository;

    public PrintingResponse create(PrintingCreateRequest request) {

        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        CardSet cardSet = setRepository.findById(request.getSetId())
                .orElseThrow(() -> new RuntimeException("CardSet not found"));

        Printing printing = new Printing();
        printing.setCard(card);
        printing.setCardSet(cardSet);
        printing.setCollectorNumber(request.getCollectorNumber());
        printing.setImageUrl(request.getImageUrl());
        printing.setRarity(request.getRarity()); // <-- Add this line

        return toResponse(printingRepository.save(printing));
    }

    public PrintingResponse getById(Long id) {
        Printing printing = printingRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("Printing not found: " + id));

        return toResponse(printing);
    }

    public List<PrintingResponse> getAll() {
        return printingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        printingRepository.deleteById(id);
    }

    private PrintingResponse toResponse(Printing printing) {
        return new PrintingResponse(
                printing.getId(),
                printing.getCard().getId(),
                printing.getCardSet().getId(),
                printing.getCollectorNumber(),
                printing.getImageUrl(),
                printing.getRarity()
        );
    }
}