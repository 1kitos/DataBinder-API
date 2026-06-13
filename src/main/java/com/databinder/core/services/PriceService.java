package com.databinder.core.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.databinder.core.dto.price.PriceSnapshotResponse;
import com.databinder.core.dto.request.PriceSnapshotCreateRequest;
import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.Printing;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.PriceSnapshotRepository;
import com.databinder.core.repositories.PrintingRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceSnapshotRepository priceSnapshotRepository;
    private final PrintingRepository printingRepository;

    public PriceSnapshotResponse create(PriceSnapshotCreateRequest request) {

        Printing printing = printingRepository.findById(request.getPrintingId())
                .orElseThrow(() -> new RuntimeException("Printing not found"));

        PriceSnapshot snapshot = new PriceSnapshot();
        snapshot.setPrinting(printing);
        snapshot.setPrice(request.getPrice());
        snapshot.setCurrency(request.getCurrency());
        snapshot.setTimestamp(Instant.now());

        return toResponse(priceSnapshotRepository.save(snapshot));
    }

    public PriceSnapshotResponse getById(Long id) {
        PriceSnapshot snapshot = priceSnapshotRepository.findById(id)
        		.orElseThrow(() -> new ResourceNotFoundException("PriceSnapshot not found: " + id));

        return toResponse(snapshot);
    }

    public List<PriceSnapshotResponse> getAll() {
        return priceSnapshotRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PriceSnapshotResponse> getByPrinting(Long printingId) {
        return priceSnapshotRepository.findByPrintingIdOrderByTimestampDesc(printingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        priceSnapshotRepository.deleteById(id);
    }

    private PriceSnapshotResponse toResponse(PriceSnapshot snapshot) {
        return new PriceSnapshotResponse(
                snapshot.getId(),
                snapshot.getPrinting().getId(),
                snapshot.getPrice(),
                snapshot.getCurrency(),
                snapshot.getTimestamp()
        );
    }
}