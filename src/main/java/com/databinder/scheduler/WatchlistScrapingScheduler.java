package com.databinder.scheduler;

import com.databinder.core.entities.Printing;
import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.Watchlist;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.enums.AlertType;
import com.databinder.core.repositories.PriceSnapshotRepository;
import com.databinder.core.repositories.WatchlistRepository;
import com.databinder.core.services.MessageService;
import com.databinder.core.services.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WatchlistScrapingScheduler {

    private final WatchlistRepository watchlistRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final PriceService priceService;
    private final MessageService messageService;

    @Value("${scraper.delay-ms:4000}")
    private long delayMs;

    @Scheduled(fixedRateString = "${scheduler.tick-rate-ms:60000}")
    @Transactional
    public void runScheduledScraping() {
        List<Watchlist> dueWatchlists = watchlistRepository.findByAutoScrapeEnabledTrue().stream()
                .filter(this::isDue)
                .toList();

        if (dueWatchlists.isEmpty()) {
            return;
        }

        log.info("Watchlists devidas neste ciclo: {}", dueWatchlists.size());

        Set<Long> alreadyProcessedPrintingIds = new HashSet<>();

        for (Watchlist watchlist : dueWatchlists) {
            processWatchlist(watchlist, alreadyProcessedPrintingIds);
            watchlist.setLastScrapedAt(Instant.now());
            watchlistRepository.save(watchlist);
        }
    }

    private boolean isDue(Watchlist watchlist) {
        if (watchlist.getLastScrapedAt() == null) {
            return true;
        }
        Instant nextDue = watchlist.getLastScrapedAt().plus(watchlist.getScrapeFrequency().getInterval());
        return Instant.now().isAfter(nextDue);
    }

    private void processWatchlist(Watchlist watchlist, Set<Long> alreadyProcessedPrintingIds) {
        List<WatchlistItem> activeItems = watchlist.getItems().stream()
                .filter(WatchlistItem::isAutoScrapeEnabled)
                .toList();

        for (WatchlistItem item : activeItems) {
            Printing printing = item.getPrinting();

            if (alreadyProcessedPrintingIds.contains(printing.getId())) {
                continue;
            }

            if (hasFreshSnapshot(printing.getId(), watchlist.getLastScrapedAt())) {
                alreadyProcessedPrintingIds.add(printing.getId());
                continue;
            }

            try {
                priceService.fetchAndSaveSnapshot(printing.getId());
                log.info("Snapshot criado com sucesso para printing {}", printing.getId());
                
                if (checkAlerts(item)) {
                    messageService.createMessage(
                        item.getWatchlist().getUser().getId(),
                        "Price Drop!",
                        item.getPrinting().getCard().getName() + " reached a new lowest price."
                    );
                }
                
            } catch (Exception e) {
                log.error("Falha ao fazer scraping da printing {}: {}", printing.getId(), e.getMessage());
            }

            alreadyProcessedPrintingIds.add(printing.getId());
            sleep();
        }
    }

    private boolean hasFreshSnapshot(Long printingId, Instant watchlistLastScrapedAt) {
        if (watchlistLastScrapedAt == null) {
            return false;
        }

        return priceSnapshotRepository.findTopByPrintingIdOrderByTimestampDesc(printingId)
                .map(PriceSnapshot::getTimestamp)
                .map(lastSnapshotTime -> lastSnapshotTime.isAfter(watchlistLastScrapedAt))
                .orElse(false);
    }

    private void sleep() {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private boolean checkAlerts(WatchlistItem item) {

        if (!Boolean.TRUE.equals(item.getAlertEnabled())) {
            return false;
        }

        if (!item.getAlerts().contains(AlertType.NEW_LOWEST_PRICE)) {
            return false;
        }

        List<PriceSnapshot> snapshots =
                priceSnapshotRepository.findTop2ByPrintingIdOrderByTimestampDesc(
                        item.getPrinting().getId());

        if (snapshots.size() < 2) {
            return false;
        }

        PriceSnapshot current = snapshots.get(0);
        PriceSnapshot previous = snapshots.get(1);

        return current.getFromPrice()
                .compareTo(previous.getFromPrice()) < 0;
    }
    
}