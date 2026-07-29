package com.databinder.core.enums;

public enum Country {

    AT("Austria"),
    BE("Belgium"),
    BG("Bulgaria"),
    CH("Switzerland"),
    CY("Cyprus"),
    CZ("Czech Republic"),
    DE("Germany"),
    DK("Denmark"),
    EE("Estonia"),
    ES("Spain"),
    FI("Finland"),
    FR("France"),
    GB("United Kingdom"),
    GR("Greece"),
    HR("Croatia"),
    HU("Hungary"),
    IE("Ireland"),
    IS("Iceland"),
    IT("Italy"),
    LI("Liechtenstein"),
    LT("Lithuania"),
    LU("Luxembourg"),
    LV("Latvia"),
    MT("Malta"),
    NL("Netherlands"),
    NO("Norway"),
    PL("Poland"),
    PT("Portugal"),
    RO("Romania"),
    SE("Sweden"),
    SI("Slovenia"),
    SK("Slovakia"), 
    SG("Singapore"),
    CA("Canada"),
    JP("Japan");

    private final String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
