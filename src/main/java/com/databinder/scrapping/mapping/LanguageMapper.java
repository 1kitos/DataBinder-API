package com.databinder.scrapping.mapping;

import java.util.Map;

import com.databinder.core.enums.Language;

public final class LanguageMapper {

    private static final Map<String, Language> MAP = Map.ofEntries(
            Map.entry("English", Language.EN),
            Map.entry("French", Language.FR),
            Map.entry("German", Language.DE),
            Map.entry("Spanish", Language.ES),
            Map.entry("Italian", Language.IT),
            Map.entry("Chinese", Language.CH),
            Map.entry("Japanese", Language.JP),
            Map.entry("Portuguese", Language.PT)
    );

    private LanguageMapper() {
    }

    public static Language fromCardmarket(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return MAP.get(value);
    }
}