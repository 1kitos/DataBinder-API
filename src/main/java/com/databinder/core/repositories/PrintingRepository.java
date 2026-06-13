package com.databinder.core.repositories;

import com.databinder.core.entities.Printing;
import org.springframework.data.jpa.repository.JpaRepository;

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
}