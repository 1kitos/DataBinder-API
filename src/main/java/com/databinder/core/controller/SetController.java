package com.databinder.core.controller;

import com.databinder.core.dto.request.SetCreateRequest;
import com.databinder.core.dto.set.SetResponse;
import com.databinder.core.services.SetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sets")
@RequiredArgsConstructor
@Tag(name = "Sets", description = "CRUD operations for card sets")
public class SetController {

    private final SetService setService;

    @PostMapping
    public SetResponse create(@Valid @RequestBody SetCreateRequest request) {
        return setService.create(request);
    }

    @GetMapping("/{id}")
    public SetResponse getById(@PathVariable Long id) {
        return setService.getById(id);
    }

    @GetMapping
    public List<SetResponse> getAll() {
        return setService.getAll();
    }

    @PutMapping("/{id}")
    public SetResponse update(@PathVariable Long id,
                              @Valid @RequestBody SetCreateRequest request) {
        return setService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        setService.delete(id);
    }
}