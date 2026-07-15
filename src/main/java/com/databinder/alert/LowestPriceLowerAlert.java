package com.databinder.alert;

import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.enums.AlertType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LowestPriceLowerAlert implements IAlert {

    @Override
    public AlertType getType() {
        return AlertType.LOWEST_PRICE_LOWER;
    }

    @Override
    public boolean shouldTrigger(WatchlistItem item) {
        List<PriceSnapshot> snapshots = item.getPrinting().getPriceSnapshots();
        if (snapshots.size() < 2) {
            return false;
        }
        PriceSnapshot last = snapshots.get(snapshots.size() - 1);
        PriceSnapshot previousToLast = snapshots.get(snapshots.size() - 2);
        return last.getFromPrice().compareTo(previousToLast.getFromPrice()) < 0;
    }

    @Override
    public String buildHeader(WatchlistItem item) {
        return "Lowest Price Drop!";
    }

    @Override
    public String buildBody(WatchlistItem item) {
        return item.getPrinting().getCard().getName() + " lowered its lowest price.";
    }
}