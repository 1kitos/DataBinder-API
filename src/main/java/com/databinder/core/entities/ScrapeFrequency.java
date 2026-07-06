package com.databinder.core.entities;

import java.time.Duration;

public enum ScrapeFrequency {
    EVERY_MINUTE(Duration.ofMinutes(1)),
    HOURLY(Duration.ofHours(1)),
    DAILY(Duration.ofDays(1)),
    WEEKLY(Duration.ofDays(7));

    private final Duration interval;

    ScrapeFrequency(Duration interval) {
        this.interval = interval;
    }

    public Duration getInterval() {
        return interval;
    }
}