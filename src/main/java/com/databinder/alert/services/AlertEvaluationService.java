package com.databinder.alert.services;

import com.databinder.alert.IAlert;
import com.databinder.core.entities.User;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final List<IAlert> alertHandlers;
    private final MessageService messageService;

    public void evaluateAlerts(WatchlistItem item) {
        if (!Boolean.TRUE.equals(item.getAlertEnabled())) {
            return;
        }

        User toUser = item.getWatchlist().getUser();

        for (IAlert alert : alertHandlers) {
            if (!item.getAlerts().contains(alert.getType())) {
                continue;
            }
            if (alert.shouldTrigger(item)) {
                messageService.save(alert.buildMessage(item, toUser));
            }
        }
    }
}