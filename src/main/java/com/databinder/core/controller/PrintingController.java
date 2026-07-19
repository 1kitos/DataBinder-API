package com.databinder.core.controller;

import com.databinder.core.dto.PrintingResponse;
import com.databinder.core.dto.request.PrintingCreateRequest;
import com.databinder.core.services.PrintingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/printings")
@RequiredArgsConstructor
@Tag(name = "Printings", description = "CRUD operations for card printings")
public class PrintingController {

    private final PrintingService printingService;

    @PostMapping
    public PrintingResponse create(@Valid @RequestBody PrintingCreateRequest request) {
        return printingService.create(request);
    }

    @GetMapping("/{id}")
    public PrintingResponse getById(@PathVariable Long id) {
        return printingService.getById(id);
    }

    @GetMapping
    public List<PrintingResponse> getAll() {
        return printingService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        printingService.delete(id);
    }
}