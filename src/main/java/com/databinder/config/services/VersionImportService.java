package com.databinder.config.services;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.databinder.core.entities.Card;
import com.databinder.core.entities.CardSet;
import com.databinder.core.entities.CardSet.Game;
import com.databinder.core.entities.Printing;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.RarityRepository;
import com.databinder.core.repositories.SetRepository;
import com.databinder.core.utils.ErrorLogger;
import com.databinder.config.repositories.DbVersionRepository;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.CardmarketUrlBuilder;
import com.databinder.scrapping.responses.CardmarketVersionData;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VersionImportService {

    private final CardRepository cardRepository;
    private final SetRepository cardSetRepository;
    private final PrintingRepository printingRepository;
    private final DbVersionRepository dbVersionRepository;
    private final RarityRepository rarityRepository;
    
    private final CardmarketScrapingService scrapingService;
    private final CardmarketUrlBuilder cardmarketUrlBuilder;
    
    private static final Game SERVICE_GAME = Game.YUGIOH;
    
    @Transactional
    public void importCardPrintings(Long cardId) {

        try {

            Card card = cardRepository.findById(cardId)
                    .orElseThrow();


            String url = cardmarketUrlBuilder.buildVersionsUrl(
                    SERVICE_GAME,
                    cardId
            );


            List<CardmarketVersionData> versions =
                    scrapingService.fetchVersions(url);


            if (versions == null || versions.isEmpty()) {
                return;
            }


            for (CardmarketVersionData version : versions) {


                String setCode = version.setCode();

                if (setCode == null || setCode.isBlank()) {
                    continue;
                }


                CardSet cardSet = cardSetRepository
                        .findByCodeIgnoreCaseAndGame(setCode, SERVICE_GAME)
                        .orElse(null);

                if (cardSet == null) {

                    CardSet newSet = new CardSet();
                    newSet.setCode(setCode);
                    newSet.setName(version.setName());
                    newSet.setGame(SERVICE_GAME);

                    try {
                        cardSet = cardSetRepository.saveAndFlush(newSet);

                    } catch (DataIntegrityViolationException e) {

                        // Outra thread criou o CardSet entretanto
                        cardSet = cardSetRepository
                                .findByCodeIgnoreCaseAndGame(setCode, SERVICE_GAME)
                                .orElseThrow();
                    }
                }

                Printing printing = printingRepository
                        .findByCard_IdAndCardSet_IdAndVersionNumber(
                                card.getId(),
                                cardSet.getId(),
                                version.versionNumber()
                        )
                        .orElse(null);

                if (printing == null) {
                    printing = new Printing();
                    printing.setCard(card);
                    printing.setCardSet(cardSet);
                    printing.setVersionNumber(version.versionNumber());
                }

                printing.updateAttributes(version);

                printingRepository.save(printing);



                printing.updateAttributes(version);

                printingRepository.save(printing);
            }


        } catch (Exception e) {

            ErrorLogger.log(e, cardId);

            throw e;
        }
    }
}