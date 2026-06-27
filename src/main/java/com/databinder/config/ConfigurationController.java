package com.databinder.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.databinder.config.services.YugiohConfigService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/configuration")
@RequiredArgsConstructor
@Tag(name = "Config", description = "Configuration Control")
public class ConfigurationController {

    private final YugiohConfigService yugiohConfigService;

    @PostMapping("/yugioh")
    public ResponseEntity<String> configureYugioh() {
        yugiohConfigService.importAll();
        return ResponseEntity.ok("Yugioh import completed");
    }
}
