package com.databinder.config.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        // carrega em memória
        Map<String, Card> cardsByName = cardRepository.findAll()
            .stream().collect(Collectors.toMap(Card::getName, c -> c));

        Map<String, CardSet> setsByCode = cardSetRepository.findAll()
            .stream().collect(Collectors.toMap(CardSet::getCode, s -> s));

        List<Card> newCards = new ArrayList<>();
        List<CardSet> newSets = new ArrayList<>();

        // 1o passe — cartas e sets
        for (JsonNode cardNode : root.get("data")) {
            String name = cardNode.get("name").asText();
            String oracleText = cardNode.has("desc") ? cardNode.get("desc").asText() : null;

            cardsByName.computeIfAbsent(name, n -> {
                Card c = new Card();
                c.setName(n);
                c.setOracleText(oracleText);
                newCards.add(c);
                return c;
            });

            if (!cardNode.has("card_sets")) continue;

            for (JsonNode setNode : cardNode.get("card_sets")) {
                String code = setNode.get("set_code").asText();
                String setCode = code.contains("-") ? code.substring(0, code.indexOf("-")) : code;
                String setName = setNode.get("set_name").asText();

                setsByCode.computeIfAbsent(setCode, sc -> {
                    CardSet cs = new CardSet();
                    cs.setName(setName);
                    cs.setCode(sc);
                    cs.setGame(Game.YUGIOH);
                    newSets.add(cs);
                    return cs;
                });
            }
        }

        // persiste cartas e sets — todos têm ID a partir daqui
        cardRepository.saveAll(newCards);
        cardSetRepository.saveAll(newSets);

        // carrega printings existentes em memória
        Set<String> existingPrintings = printingRepository.findAll()
            .stream()
            .map(p -> p.getCard().getId() + "-" + p.getCardSet().getId())
            .collect(Collectors.toSet());

        List<Printing> newPrintings = new ArrayList<>();

        // 2o passe — printings
        for (JsonNode cardNode : root.get("data")) {
            String name = cardNode.get("name").asText();
            Card card = cardsByName.get(name);

            if (!cardNode.has("card_sets")) continue;

            String imageUrl = cardNode.has("card_images")
                ? cardNode.get("card_images").get(0).get("image_url").asText()
                : null;

            for (JsonNode setNode : cardNode.get("card_sets")) {
                String code = setNode.get("set_code").asText();
                String setCode = code.contains("-") ? code.substring(0, code.indexOf("-")) : code;
                CardSet cardSet = setsByCode.get(setCode);

                String key = card.getId() + "-" + cardSet.getId();
                if (!existingPrintings.contains(key)) {
                    Printing printing = new Printing();
                    printing.setCard(card);
                    printing.setCardSet(cardSet);
                    printing.setCollectorNumber(code);
                    printing.setImageUrl(imageUrl);
                    newPrintings.add(printing);
                    existingPrintings.add(key);
                }
            }
        }

        printingRepository.saveAll(newPrintings);

        // atualiza versão
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
