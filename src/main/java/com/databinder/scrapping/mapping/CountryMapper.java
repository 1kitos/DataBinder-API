package com.databinder.scrapping.mapping;


import com.databinder.core.enums.Country;

import java.util.Map;

public final class CountryMapper {

    private static final Map<String, Country> MAP = Map.ofEntries(
            Map.entry("Austria", Country.AT),
            Map.entry("Belgium", Country.BE),
            Map.entry("Bulgaria", Country.BG),
            Map.entry("Canada", Country.CA),
            Map.entry("Croatia", Country.HR),
            Map.entry("Cyprus", Country.CY),
            Map.entry("Czech Republic", Country.CZ),
            Map.entry("Denmark", Country.DK),
            Map.entry("Estonia", Country.EE),
            Map.entry("Finland", Country.FI),
            Map.entry("France", Country.FR),
            Map.entry("Germany", Country.DE),
            Map.entry("Greece", Country.GR),
            Map.entry("Hungary", Country.HU),
            Map.entry("Iceland", Country.IS),
            Map.entry("Ireland", Country.IE),
            Map.entry("Italy", Country.IT),
            Map.entry("Japan", Country.JP),
            Map.entry("Latvia", Country.LV),
            Map.entry("Liechtenstein", Country.LI),
            Map.entry("Lithuania", Country.LT),
            Map.entry("Luxembourg", Country.LU),
            Map.entry("Malta", Country.MT),
            Map.entry("Netherlands", Country.NL),
            Map.entry("Norway", Country.NO),
            Map.entry("Poland", Country.PL),
            Map.entry("Portugal", Country.PT),
            Map.entry("Romania", Country.RO),
            Map.entry("Singapore", Country.SG),
            Map.entry("Slovakia", Country.SK),
            Map.entry("Slovenia", Country.SI),
            Map.entry("Spain", Country.ES),
            Map.entry("Sweden", Country.SE),
            Map.entry("Switzerland", Country.CH),
            Map.entry("United Kingdom", Country.GB)
    );

    private CountryMapper() {
    }

    public static Country fromCardmarket(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return MAP.get(value);
    }
}
