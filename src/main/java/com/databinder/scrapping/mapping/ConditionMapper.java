package com.databinder.scrapping.mapping;

import java.util.Map;

import com.databinder.core.enums.Condition;

public final class ConditionMapper {

    private static final Map<String, Condition> MAP = Map.ofEntries(
            Map.entry("Mint", Condition.MINT),
            Map.entry("Near Mint", Condition.NEAR_MINT),
            Map.entry("Excellent", Condition.EXCELLENT),
            Map.entry("Good", Condition.GOOD),
            Map.entry("Light Played", Condition.LIGHT_PLAYED),
            Map.entry("Played", Condition.PLAYED),
            Map.entry("Poor", Condition.POOR)
    );

    private ConditionMapper() {
    }

    public static Condition fromCardmarket(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return MAP.get(value);
    }
}
