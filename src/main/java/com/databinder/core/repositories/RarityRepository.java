package com.databinder.core.repositories;

import com.databinder.core.entities.Rarity;
import com.databinder.core.entities.CardSet.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RarityRepository extends JpaRepository<Rarity, Long> {

    Optional<Rarity> findByNameIgnoreCaseAndGame(String name, Game game);

    List<Rarity> findByGameOrderBySortOrderAsc(Game game);

    @Modifying
    @Transactional
    @Query("DELETE FROM Rarity r WHERE r.game = :game")
    void deleteAllByGame(@Param("game") Game game);

    @Query("SELECT MAX(r.sortOrder) FROM Rarity r WHERE r.game = :game")
    Optional<Integer> findMaxSortOrderByGame(@Param("game") Game game);
    
    // Find by name (case-sensitive)
    Optional<Rarity> findByName(String name);
    
    // Optional: Find by name and game (if you want to check both)
    Optional<Rarity> findByNameAndGame(String name, Game game);
    
    void deleteByGame(Game game);
 }