package com.databinder.config.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.databinder.config.entities.DbVersion;
import com.databinder.config.repositories.DbVersionRepository;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.entities.Printing;
import com.databinder.core.entities.Rarity;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.RarityRepository;
import com.databinder.core.repositories.SetRepository;
import com.databinder.excel.RarityExcelModel;
import com.databinder.excel.RarityExcelReader;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.CardmarketUrlBuilder;
import com.databinder.scrapping.responses.CardmarketVersionData;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YugiohConfigService {

    private final CardRepository cardRepository;
    private final SetRepository cardSetRepository;
    private final PrintingRepository printingRepository;
    private final DbVersionRepository dbVersionRepository;
    private final RarityRepository rarityRepository;
    
    private final CardmarketScrapingService scrapingService;
    private final CardmarketUrlBuilder cardmarketUrlBuilder;
    
    private final VersionImportService versionImportService;
    
    private final ExecutorService versionImportExecutor;

    private final WebClient webClient = WebClient.builder()
    	    .codecs(config -> config.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))
    	    .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    	    .build();
    private static final String API_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php";
    private static final String VERSION_URL = "https://db.ygoprodeck.com/api/v7/checkDBVer.php";
    
    private static final Game SERVICE_GAME = Game.YUGIOH;
    private static final String RARITY_EXCEL = "static/Databinder_rarities.xlsx";

    public boolean hasNewVersion() {
        JsonNode versionNode = webClient.get()
            .uri(VERSION_URL)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        if (versionNode == null || !versionNode.isArray() || versionNode.size() == 0) return true;

        String remoteVersion = versionNode.get(0).get("database_version").asText();

        return dbVersionRepository.findByGame(Game.YUGIOH)
            .map(v -> !v.getVersion().equals(remoteVersion))
            .orElse(true);
    }

    public void importCards() {

        JsonNode root = webClient.get()
                .uri(API_URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (root == null || !root.has("data")) {
            return;
        }

        Map<String, Card> cardsByName = cardRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Card::getName,
                        c -> c
                ));

        List<Card> cardsToPersist = new ArrayList<>();

        for (JsonNode cardNode : root.get("data")) {

            String name = cardNode.get("name").asText();

            String oracleText = cardNode.has("desc")
                    ? cardNode.get("desc").asText()
                    : null;


            Card card = cardsByName.get(name);

            if (card == null) {
                card = new Card();
                card.setName(name);

                cardsByName.put(name, card);
            }

            // update fields
            card.setOracleText(oracleText);

            cardsToPersist.add(card);
        }

        cardRepository.saveAll(cardsToPersist);
    }

    @Transactional
    public void importRarities() {
        try {
            System.out.println("========== STARTING RARITY IMPORT ==========");
            System.out.println("Looking for Excel file: " + RARITY_EXCEL);
            
            RarityExcelReader reader = new RarityExcelReader();
            List<RarityExcelModel> rarityModels = reader.readRaritiesFromSheet(RARITY_EXCEL, "YuGiOh");
            
            System.out.println("Total rows read from Excel: " + rarityModels.size());
            
            if (rarityModels.isEmpty()) {
                System.out.println("⚠️ WARNING: No rarities found in Excel file!");
                System.out.println("   Check that the file exists and sheet 'YuGiOh' has data.");
                return;
            }
            
            // Print first few rows for debugging
            System.out.println("\n📊 First 5 rows from Excel:");
            for (int i = 0; i < Math.min(5, rarityModels.size()); i++) {
                RarityExcelModel model = rarityModels.get(i);
                System.out.println("   Row " + (i + 1) + ":");
                System.out.println("      Rarity Name: '" + model.getRarityName() + "'");
                System.out.println("      Code:        '" + model.getRarityCode() + "'");
                System.out.println("      Slug:        '" + model.getRaritySlug() + "'");
                System.out.println("      Hierarchy:   '" + model.getHierarchy() + "'");
            }
            System.out.println();
            
            // Use AtomicInteger for mutable counters inside lambda
            AtomicInteger inserted = new AtomicInteger(0);
            AtomicInteger updated = new AtomicInteger(0);
            
            int rowNumber = 0;
            for (RarityExcelModel rarityModel : rarityModels) {
                rowNumber++;
                
                // Print each row being processed (you can comment this out after debugging)
                System.out.println("Processing row " + rowNumber + ":");
                System.out.println("   Name: '" + rarityModel.getRarityName() + "'");
                System.out.println("   Code: '" + rarityModel.getRarityCode() + "'");
                System.out.println("   Slug: '" + rarityModel.getRaritySlug() + "'");
                System.out.println("   Hierarchy: '" + rarityModel.getHierarchy() + "'");
                
                // Check for empty values
                if (rarityModel.getRarityName() == null || rarityModel.getRarityName().isEmpty()) {
                    System.out.println("   ⚠️ SKIPPING: Rarity name is empty!");
                    continue;
                }
                
                if (rarityModel.getHierarchy() == null || rarityModel.getHierarchy().isEmpty()) {
                    System.out.println("   ⚠️ SKIPPING: Hierarchy is empty for '" + rarityModel.getRarityName() + "'!");
                    continue;
                }
                
                try {
                    Integer.parseInt(rarityModel.getHierarchy());
                } catch (NumberFormatException e) {
                    System.out.println("   ⚠️ SKIPPING: Invalid hierarchy value '" + 
                        rarityModel.getHierarchy() + "' for '" + rarityModel.getRarityName() + "'!");
                    continue;
                }
                
                Rarity rarityEntity = rarityRepository.findByNameAndGame(
                        rarityModel.getRarityName(), 
                        SERVICE_GAME
                    )
                    .map(existing -> {
                        System.out.println("   🔄 UPDATING existing rarity: '" + 
                            rarityModel.getRarityName() + "' (id: " + existing.getId() + ")");
                        // UPDATE: Modify existing
                        existing.setCode(rarityModel.getRarityCode());
                        existing.setSlug(rarityModel.getRaritySlug());
                        existing.setSortOrder(Integer.parseInt(rarityModel.getHierarchy()));
                        updated.incrementAndGet();
                        return existing;
                    })
                    .orElseGet(() -> {
                        System.out.println("   ✨ CREATING new rarity: '" + 
                            rarityModel.getRarityName() + "'");
                        inserted.incrementAndGet();
                        return Rarity.builder()
                                .name(rarityModel.getRarityName())
                                .code(rarityModel.getRarityCode())
                                .slug(rarityModel.getRaritySlug())
                                .sortOrder(Integer.parseInt(rarityModel.getHierarchy()))
                                .game(SERVICE_GAME)
                                .build();
                    });
                
                rarityRepository.save(rarityEntity);
                System.out.println("   ✅ Saved: '" + rarityEntity.getName() + 
                    "' (id: " + rarityEntity.getId() + ")");
                System.out.println();
            }
            
            System.out.println("==========================================");
            System.out.println("✅ Successfully imported rarities: " + 
                              inserted.get() + " inserted, " + 
                              updated.get() + " updated");
            
            // Verify the import
            long total = rarityRepository.count();
            System.out.println("📊 Total rarities in database: " + total);
            
            // List all rarities from database
            List<Rarity> allRarities = rarityRepository.findAll();
            if (!allRarities.isEmpty()) {
                System.out.println("\n📋 Rarities in database:");
                for (Rarity r : allRarities) {
                    System.out.println("   id=" + r.getId() + 
                        ", name='" + r.getName() + 
                        "', code='" + r.getCode() + 
                        "', slug='" + r.getSlug() + 
                        "', sortOrder=" + r.getSortOrder() + 
                        ", game=" + r.getGame());
                }
            }
            System.out.println("==========================================");
            
        } catch (Exception e) {
            System.err.println("❌ Error Reading Rarities From Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public void importPrintings() {

        List<Card> allCards = cardRepository.findAll();

        List<Future<?>> futures = new ArrayList<>();

        int total = allCards.size();

        AtomicInteger submitted = new AtomicInteger();
        AtomicInteger completed = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (Card card : allCards) {

            int current = submitted.incrementAndGet();

            System.out.printf(
                    "Submitting %d/%d - %s%n",
                    current,
                    total,
                    card.getName());

            futures.add(
                    versionImportExecutor.submit(() -> {

                        try {
                            versionImportService.importCardPrintings(card.getId());
                        } finally {

                            int done = completed.incrementAndGet();

                            if (done % 100 == 0 || done == total) {

                                long elapsedMs = System.currentTimeMillis() - start;

                                double elapsedMinutes = elapsedMs / 60000.0;

                                double cardsPerMinute =
                                        elapsedMinutes > 0
                                                ? done / elapsedMinutes
                                                : 0;

                                double etaMinutes =
                                        cardsPerMinute > 0
                                                ? (total - done) / cardsPerMinute
                                                : 0;

                                System.out.printf(
                                        "Progress: %d/%d (%.1f%%) | Elapsed: %.1f min | ETA: %.1f min%n",
                                        done,
                                        total,
                                        done * 100.0 / total,
                                        elapsedMinutes,
                                        etaMinutes
                                );
                            }
                        }
                    })
            );
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long elapsedMs = System.currentTimeMillis() - start;

        System.out.printf(
                "Version import complete. Imported %d cards in %.2f minutes.%n",
                total,
                elapsedMs / 60000.0
        );
    }
    
    
    
      
    @Transactional
    public void clearRarities() {
        rarityRepository.deleteByGame(SERVICE_GAME);
        System.out.println("Cleared all rarities for game: " + SERVICE_GAME);
    }
    

}