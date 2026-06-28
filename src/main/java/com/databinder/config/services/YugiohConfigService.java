package com.databinder.config.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.databinder.config.entities.DbVersion;
import com.databinder.config.repositories.DbVersionRepository;
import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.entities.Printing;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.SetRepository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YugiohConfigService {

    private final CardRepository cardRepository;
    private final SetRepository cardSetRepository;
    private final PrintingRepository printingRepository;
    private final DbVersionRepository dbVersionRepository;

    private final WebClient webClient = WebClient.builder()
        .codecs(config -> config.defaultCodecs().maxInMemorySize(50 * 1024 * 1024))
        .build();

    private static final String API_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php";
    private static final String VERSION_URL = "https://db.ygoprodeck.com/api/v7/checkDBVer.php";

    public boolean hasNewVersion() {
        JsonNode versionNode = webClient.get()
            .uri(VERSION_URL)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        if (versionNode == null || !versionNode.has("database_version")) return true;

        String remoteVersion = versionNode.get("database_version").asText();

        return dbVersionRepository.findByGame(Game.YUGIOH)
            .map(v -> !v.getVersion().equals(remoteVersion))
            .orElse(true);
    }

    public void importAll() {
        JsonNode versionNode = webClient.get()
            .uri(VERSION_URL)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        JsonNode root = webClient.get()
            .uri(API_URL)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        if (root == null || !root.has("data")) return;

        // 1. Load everything into memory maps
        Map<String, Card> cardsByName = cardRepository.findAll()
            .stream().collect(Collectors.toMap(Card::getName, c -> c));

        Map<String, CardSet> setsByCode = cardSetRepository.findAll()
            .stream().collect(Collectors.toMap(CardSet::getCode, s -> s));

        List<Card> cardsToPersist = new ArrayList<>();
        List<CardSet> setsToPersist = new ArrayList<>();

        // 1st Pass — Upsert Cards and Sets
        for (JsonNode cardNode : root.get("data")) {
            String name = cardNode.get("name").asText();
            String oracleText = cardNode.has("desc") ? cardNode.get("desc").asText() : null;

            Card card = cardsByName.get(name);
            if (card == null) {
                card = new Card();
                card.setName(name);
                card.setOracleText(oracleText);
                cardsByName.put(name, card);
                cardsToPersist.add(card);
            } else {
                // Future-proofing: Update existing card fields if they changed
                card.setOracleText(oracleText);
                cardsToPersist.add(card); 
            }

            if (!cardNode.has("card_sets")) continue;

            for (JsonNode setNode : cardNode.get("card_sets")) {
                String code = setNode.get("set_code").asText();
                String setCode = code.contains("-") ? code.substring(0, code.indexOf("-")) : code;
                String setName = setNode.get("set_name").asText();

                CardSet cardSet = setsByCode.get(setCode);
                if (cardSet == null) {
                    cardSet = new CardSet();
                    cardSet.setName(setName);
                    cardSet.setCode(setCode);
                    cardSet.setGame(Game.YUGIOH);
                    setsByCode.put(setCode, cardSet);
                    setsToPersist.add(cardSet);
                } else {
                    // Update field if needed
                    cardSet.setName(setName);
                    setsToPersist.add(cardSet);
                }
            }
        }

        // Persist Cards and Sets so they definitely have IDs for the next phase
        cardRepository.saveAll(cardsToPersist);
        cardSetRepository.saveAll(setsToPersist);

        // 2. Load existing printings into a map using the compound key
        Map<String, Printing> existingPrintingsMap = printingRepository.findAll()
            .stream()
            .collect(Collectors.toMap(
                p -> p.getCard().getId() + "-" + p.getCardSet().getId(),
                p -> p,
                (existing, replacement) -> existing // Fallback merger function
            ));

        List<Printing> printingsToPersist = new ArrayList<>();

        // 2nd Pass — Upsert Printings
        for (JsonNode cardNode : root.get("data")) {
            String name = cardNode.get("name").asText();
            Card card = cardsByName.get(name);

            if (!cardNode.has("card_sets") || card == null) continue;

            String imageUrl = cardNode.has("card_images")
                ? cardNode.get("card_images").get(0).get("image_url").asText()
                : null;

            for (JsonNode setNode : cardNode.get("card_sets")) {
                String code = setNode.get("set_code").asText();
                String setCode = code.contains("-") ? code.substring(0, code.indexOf("-")) : code;
                CardSet cardSet = setsByCode.get(setCode);

                if (cardSet == null) continue;

                String rarity = setNode.has("set_rarity") ? setNode.get("set_rarity").asText() : null;
                String key = card.getId() + "-" + cardSet.getId();

                Printing printing = existingPrintingsMap.get(key);
                
                if (printing == null) {
                    // Create new printing if it doesn't exist
                    printing = new Printing();
                    printing.setCard(card);
                    printing.setCardSet(cardSet);
                    existingPrintingsMap.put(key, printing);
                }
                
                // --- Dynamic Field Alignment ---
                // This updates old records with missing rarities AND saves new records!
                printing.setCollectorNumber(code);
                printing.setImageUrl(imageUrl);
                printing.setRarity(rarity); 
                
                // Add any future entity properties here:
                // printing.setSomeFutureField(setNode.get("future").asText());

                printingsToPersist.add(printing);
            }
        }

        // Saves everything (executes INSERTs for new ones and UPDATEs for existing ones)
        printingRepository.saveAll(printingsToPersist);

        // 3. Update Database Version Tracker
        if (versionNode != null && versionNode.has("database_version")) {
            String remoteVersion = versionNode.get("database_version").asText();
            DbVersion dbVersion = dbVersionRepository.findByGame(Game.YUGIOH)
                .orElseGet(() -> {
                    DbVersion v = new DbVersion();
                    v.setGame(Game.YUGIOH);
                    return v;
                });
            dbVersion.setVersion(remoteVersion);
            dbVersion.setLastUpdated(Instant.now());
            dbVersionRepository.save(dbVersion);
        }
    }
}