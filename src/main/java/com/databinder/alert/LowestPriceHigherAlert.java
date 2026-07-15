package com.databinder.alert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.databinder.core.entities.PriceSnapshot;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.enums.AlertType;

@Component
public class LowestPriceHigherAlert implements IAlert {

	@Override
	public AlertType getType() {
		return AlertType.LOWEST_PRICE_HIGHER;
	}

	@Override
	public boolean shouldTrigger(WatchlistItem item) {
        List<PriceSnapshot> snapshots = item.getPrinting().getPriceSnapshots();
        if (snapshots.size() < 2) {
            return false;
        }
        PriceSnapshot last = snapshots.get(snapshots.size() - 1);
        PriceSnapshot previousToLast = snapshots.get(snapshots.size() - 2);
        return last.getFromPrice().compareTo(previousToLast.getFromPrice()) > 0;
    }

	@Override
    public String buildHeader(WatchlistItem item) {
        return "Lowest Price Rise!";
    }

	 @Override
	    public String buildBody(WatchlistItem item) {
	        return item.getPrinting().getCard().getName() + " increased its lowest price.";
	    }

}
