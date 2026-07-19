package com.databinder.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.databinder.core.dto.RarityResponse;
import com.databinder.core.dto.request.RarityCreateRequest;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.services.RarityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/rarities")
@RequiredArgsConstructor
@Tag(name = "Rarities", description = "Manage card rarities")
public class RarityController {

    private final RarityService rarityService;

    @Operation(summary = "Create a new rarity")
    @PostMapping
    public RarityResponse create(@Valid @RequestBody RarityCreateRequest request) {
        return rarityService.create(request);
    }

    @Operation(summary = "Get rarity by id")
    @GetMapping("/{id}")
    public RarityResponse getById(@PathVariable Long id) {
        return rarityService.getById(id);
    }

    @Operation(summary = "List all rarities")
    @GetMapping
    public List<RarityResponse> getAll() {
        return rarityService.getAll();
    }

    @Operation(summary = "Get rarities by game")
    @GetMapping("/game/{game}")
    public List<RarityResponse> getByGame(@PathVariable Game game) {
        return rarityService.getByGame(game);
    }

    @Operation(summary = "Update rarity")
    @PutMapping("/{id}")
    public RarityResponse update(
            @PathVariable Long id,
            @Valid @RequestBody RarityCreateRequest request
    ) {
        return rarityService.update(id, request);
    }

    @Operation(summary = "Delete rarity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rarityService.delete(id);
    }

    @Operation(summary = "Delete all rarities by game")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/game/{game}")
    public void deleteByGame(@PathVariable String game) {
        rarityService.deleteByGame(game);
    }
}