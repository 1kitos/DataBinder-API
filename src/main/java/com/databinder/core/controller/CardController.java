package com.databinder.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.databinder.core.dto.CardDetailsResponse;
import com.databinder.core.dto.CardResponse;
import com.databinder.core.dto.request.CardCreateRequest;
import com.databinder.core.services.CardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Manage trading cards")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Create a new card")
    @PostMapping
    public CardResponse create(@Valid @RequestBody CardCreateRequest request) {
        return cardService.create(request);
    }

    @Operation(summary = "Get card by id")
    @GetMapping("/{id}")
    public CardResponse getById(@PathVariable Long id) {
        return cardService.getById(id);
    }
    
    @GetMapping("/details/{id}")
    public CardDetailsResponse getCardDetails(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return cardService.getCardDetails(id, page, pageSize);
    }

    @Operation(summary = "List all cards")
    @GetMapping
    public List<CardResponse> getAll() {
        return cardService.getAll();
    }

    @Operation(summary = "Update card")
    @PutMapping("/{id}")
    public CardResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CardCreateRequest request
    ) {
        return cardService.update(id, request);
    }

    @Operation(summary = "Delete card")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cardService.delete(id);
    }
}