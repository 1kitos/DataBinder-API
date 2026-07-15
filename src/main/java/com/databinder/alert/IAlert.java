package com.databinder.alert;

import com.databinder.core.entities.Message;
import com.databinder.core.entities.User;
import com.databinder.core.entities.WatchlistItem;
import com.databinder.core.enums.AlertType;
import com.databinder.core.enums.MessageStatus;

import java.time.Instant;

public interface IAlert {
    AlertType getType();
    boolean shouldTrigger(WatchlistItem item);
    String buildHeader(WatchlistItem item);
    String buildBody(WatchlistItem item);

    default Message buildMessage(WatchlistItem item, User toUser) {
        Message result = new Message();
        result.setToUser(toUser);
        result.setHeader(buildHeader(item));
        result.setBody(buildBody(item));
        result.setCreatedAt(Instant.now());
        result.setRead(false);
        result.setStatus(MessageStatus.PENDING);
        return result;
    }
}