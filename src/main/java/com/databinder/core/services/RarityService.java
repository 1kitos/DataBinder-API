package com.databinder.core.services;

import com.databinder.core.dto.RarityResponse;
import com.databinder.core.dto.request.RarityCreateRequest;
import com.databinder.core.entities.Rarity;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.RarityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RarityService {

    private final RarityRepository rarityRepository;

    @Transactional
    public RarityResponse create(RarityCreateRequest request) {
        // Check if rarity with same name and game already exists
        rarityRepository.findByNameAndGame(request.getName(), request.getGame())
            .ifPresent(existing -> {
                throw new RuntimeException("Rarity with name '" + request.getName() + 
                    "' and game '" + request.getGame() + "' already exists");
            });

        Rarity rarity = new Rarity();
        rarity.setName(request.getName());
        rarity.setCode(request.getCode());
        rarity.setSlug(request.getSlug());
        rarity.setSortOrder(request.getSortOrder());
        rarity.setGame(request.getGame());

        Rarity saved = rarityRepository.save(rarity);
        log.info("Created rarity: {} for game: {}", saved.getName(), saved.getGame());
        
        return toResponse(saved);
    }

    public RarityResponse getById(Long id) {
        Rarity rarity = rarityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rarity not found: " + id));
        
        return toResponse(rarity);
    }

    public List<RarityResponse> getAll() {
        return rarityRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public List<RarityResponse> getByGame(Game game) {
        return rarityRepository.findByGameOrderBySortOrderAsc(game)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public RarityResponse update(Long id, RarityCreateRequest request) {
        Rarity rarity = rarityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rarity not found: " + id));

        // Check if another rarity with same name and game exists (excluding this one)
        rarityRepository.findByNameAndGame(request.getName(), request.getGame())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("Rarity with name '" + request.getName() + 
                        "' and game '" + request.getGame() + "' already exists");
                }
            });

        rarity.setName(request.getName());
        rarity.setCode(request.getCode());
        rarity.setSlug(request.getSlug());
        rarity.setSortOrder(request.getSortOrder());
        rarity.setGame(request.getGame());

        Rarity updated = rarityRepository.save(rarity);
        log.info("Updated rarity with id: {}", id);
        
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!rarityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rarity not found: " + id);
        }
        
        rarityRepository.deleteById(id);
        log.info("Deleted rarity with id: {}", id);
    }

    @Transactional
    public void deleteByGame(String game) {
        Game gameEnum;
        try {
            gameEnum = Game.valueOf(game.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid game: " + game);
        }
        
        rarityRepository.deleteByGame(gameEnum);
        log.info("Deleted all rarities for game: {}", game);
    }

    private RarityResponse toResponse(Rarity rarity) {
        return new RarityResponse(
            rarity.getId(),
            rarity.getName(),
            rarity.getCode(),
            rarity.getSlug(),
            rarity.getSortOrder(),
            rarity.getGame()
        );
    }
}