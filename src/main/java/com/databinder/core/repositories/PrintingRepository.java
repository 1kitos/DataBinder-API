package com.databinder.core.repositories;

import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet;
import com.databinder.core.entities.Printing;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrintingRepository extends JpaRepository<Printing, Long> {

    List<Printing> findByCard_Id(Long cardId);

    List<Printing> findByCardSet_Id(Long cardSetId);

    Optional<Printing> findByCard_IdAndCardSet_IdAndCollectorNumber(
            Long cardId,
            Long cardSetId,
            String collectorNumber
    );
    
    List<Printing> findByCard_NameIgnoreCaseAndCardSet_NameIgnoreCaseAndCardSet_Game(
            String cardName,
            String setName,
            CardSet.Game game
    );
    
    boolean existsByCardAndCardSet(Card card, CardSet cardSet);
    
    @Query(value = """
            SELECT p.* FROM printing p
            JOIN card c ON c.id = p.card_id
            JOIN sets cs ON cs.id = p.set_id
            WHERE cs.game = :game
            AND (:cardName IS NULL OR :cardName = '' OR c.name ILIKE CONCAT('%', :cardName, '%'))
            AND (:setCode IS NULL OR :setCode = '' OR cs.code = :setCode)
            AND (:rarity IS NULL OR :rarity = '' OR p.rarity = :rarity)
            ORDER BY c.name ASC, p.collector_number ASC
            """,
            countQuery = """
            SELECT count(*) FROM printing p
            JOIN card c ON c.id = p.card_id
            JOIN sets cs ON cs.id = p.set_id
            WHERE cs.game = :game
            AND (:cardName IS NULL OR :cardName = '' OR c.name ILIKE CONCAT('%', :cardName, '%'))
            AND (:setCode IS NULL OR :setCode = '' OR cs.code = :setCode)
            AND (:rarity IS NULL OR :rarity = '' OR p.rarity = :rarity)
            """,
            nativeQuery = true)
    Page<Printing> searchPrintingsAdvanced(
        @Param("game") String game,
        @Param("cardName") String cardName,
        @Param("setCode") String setCode,
        @Param("rarity") String rarity,
        Pageable pageable
    );
    
    
    @Query(value = """
    		SELECT p.*
    		FROM printing p
    		JOIN card c ON c.id = p.card_id
    		JOIN sets s ON s.id = p.set_id
    		LEFT JOIN rarities r
    		       ON LOWER(r.name) = LOWER(p.rarity)
    		      AND r.game = s.game
    		WHERE c.name ILIKE :cardName
    		AND s.name ILIKE :setName
    		AND s.game = :game
    		ORDER BY r.sort_order ASC NULLS LAST
    		""", nativeQuery = true)
    		List<Printing> findPrintingsOrdered(
    		        @Param("game") String game,
    		        @Param("cardName") String cardName,
    		        @Param("setName") String setName);
    
    
}