package com.databinder.core.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet.Game;

import java.util.Optional;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNameIgnoreCase(String name);

    @Query("""
        SELECT c FROM Card c
        WHERE EXISTS (
            SELECT 1 FROM Printing p
            JOIN p.cardSet cs
            WHERE p.card = c
            AND cs.game = :game
        )
        AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
        """)
    Page<Card> findByGameAndNameContainingIgnoreCase(
        @Param("game") Game game,
        @Param("name") String name,
        Pageable pageable
    );
    
    Optional<Card> findByName(String name);
    
    @Query(value = """
            SELECT c.* FROM card c
            WHERE EXISTS (
                SELECT 1 FROM printing p
                JOIN sets cs ON cs.id = p.set_id
                WHERE p.card_id = c.id
                AND cs.game = :game
            )
            AND c.name ILIKE CONCAT('%', :query, '%')
            ORDER BY c.name
            """,
            countQuery = """
            SELECT count(*) FROM card c
            WHERE EXISTS (
                SELECT 1 FROM printing p
                JOIN sets cs ON cs.id = p.set_id
                WHERE p.card_id = c.id
                AND cs.game = :game
            )
            AND c.name ILIKE CONCAT('%', :query, '%')
            """,
            nativeQuery = true)
    Page<Card> searchRanked(
        @Param("game") String game,
        @Param("query") String query,
        Pageable pageable
    );
}