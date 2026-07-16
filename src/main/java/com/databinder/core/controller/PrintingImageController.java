package com.databinder.core.controller;


import com.databinder.core.entities.Printing;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.PrintingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/printings")
@RequiredArgsConstructor
public class PrintingImageController {

    private final PrintingRepository printingRepository;

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Printing printing = printingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Printing not found: " + id));

        if (printing.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(printing.getImageData());
    }
}