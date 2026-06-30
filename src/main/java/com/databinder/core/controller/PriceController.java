package com.databinder.core.controller;

import com.databinder.core.dto.price.PriceSnapshotResponse;
import com.databinder.core.dto.request.PriceSnapshotCreateRequest;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.services.PriceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "Manage price history for printings")
public class PriceController {

    private final PriceService priceService;

    @PostMapping
    public PriceSnapshotResponse create(@Valid @RequestBody PriceSnapshotCreateRequest request) {
        return priceService.create(request);
    }

    @GetMapping("/{id}")
    public PriceSnapshotResponse getById(@PathVariable Long id) {
        return priceService.getById(id);
    }

    @GetMapping
    public List<PriceSnapshotResponse> getAll() {
        return priceService.getAll();
    }

    @GetMapping("/printings/{printingId}")
    public List<PriceSnapshotResponse> getByPrinting(@PathVariable Long printingId) {
        return priceService.getByPrinting(printingId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        priceService.delete(id);
    }
    
    @PostMapping("/snapshot")
    public PriceSnapshotResponse snapshot(@RequestParam Game game,
                                        @RequestParam String set,
                                        @RequestParam String cardName) {

        return priceService.getSnapshotForCard(game, set, cardName);
    }
}