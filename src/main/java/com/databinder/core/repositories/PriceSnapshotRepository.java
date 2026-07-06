package com.databinder.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.databinder.core.entities.PriceSnapshot;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {

    List<PriceSnapshot> findByPrintingId(Long printingId);

    List<PriceSnapshot> findByPrintingIdOrderByTimestampDesc(Long printingId);

    List<PriceSnapshot> findByPrintingIdAndTimestampAfter(
            Long printingId,
            Instant timestamp
    );
    
   
    Optional<PriceSnapshot> findTopByPrintingIdOrderByTimestampDesc(Long printingId);
    
    List<PriceSnapshot> findTop2ByPrintingIdOrderByTimestampDesc(Long printingId);
}
