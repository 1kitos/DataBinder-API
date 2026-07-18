package com.databinder.config;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.databinder.config.entities.DbVersion;
import com.databinder.config.services.DBConfigService;
import com.databinder.config.services.YugiohConfigService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/configuration")
@RequiredArgsConstructor
@Tag(name = "Config", description = "Configuration Control")
public class ConfigurationController {

    private final YugiohConfigService yugiohConfigService;
    private final DBConfigService dbConfigService;
    

    @PostMapping("/yugioh/cards")
    public ResponseEntity<String> configureYugioh() {
        yugiohConfigService.importCards();
        return ResponseEntity.ok("Yugioh import completed");
    }
    
    @PostMapping("/yugioh/printings")
    public ResponseEntity<String> configureYugiohPrintings()
    {
    	yugiohConfigService.importPrintings();
    	return ResponseEntity.ok("Yugioh import versions completed");
    }
    
    @PostMapping("/yugioh-rarities")
    public ResponseEntity<String> configureYugiohRarities() {
        yugiohConfigService.importRarities();
        return ResponseEntity.ok("Yugioh Rarities import completed");
    }
    
    @DeleteMapping("/yugioh-rarities")
    public ResponseEntity<String> clearYugiohRarities() {
        yugiohConfigService.clearRarities();
        return ResponseEntity.ok("Yugioh Rarities cleared successfully");
    }
    
    @GetMapping("/versions")
    public ResponseEntity<List<DbVersion>> getVersions() {
        return ResponseEntity.ok(dbConfigService.getAllVersions());
    }
    
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        dbConfigService.resetDatabase();
        return ResponseEntity.ok("Database reset completed");
    }
}
