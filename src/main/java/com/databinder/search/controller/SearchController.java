package com.databinder.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.databinder.core.dto.CardResponse;
import com.databinder.core.dto.PrintingResponse;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.search.services.SearchService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search for entities in the Database")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/cards")
    public List<CardResponse> searchCards(
            @RequestParam Game game,
            @RequestParam String query,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        return searchService.searchCards(game, query, page, pageSize);
    }
    
    
    @GetMapping("/printings/advanced")
    public List<PrintingResponse> searchPrintingsAdvanced(
            @RequestParam Game game,
            @RequestParam(required = false) String cardName,
            @RequestParam(required = false) String setCode,
            @RequestParam(required = false) String rarity,
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        return searchService.searchPrintingsAdvanced(game, cardName, setCode, rarity, page, pageSize);
    }
    
}
