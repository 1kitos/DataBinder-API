package com.databinder.core.dto.request;

import com.databinder.core.entities.CardSet.Game;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RarityCreateRequest {

    @NotBlank(message = "Rarity name is required")
    private String name;

    private String code;

    private String slug;

    @NotNull(message = "Sort order is required")
    @Positive(message = "Sort order must be positive")
    private Integer sortOrder;

    @NotNull(message = "Game is required")
    private Game game;
}