package com.databinder.core.enums;

public enum Condition {

    MINT("Mint", 1),
    NEAR_MINT("Near Mint", 2),
    EXCELLENT("Excellent", 3),
    GOOD("Good", 4),
    LIGHT_PLAYED("Light Played", 5),
    PLAYED("Played", 6),
    POOR("Poor", 7);

    private final String displayName;
    private final int code;

    Condition(String displayName, int code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCode() {
        return code;
    }

    public static Condition fromCode(int code) {
        for (Condition condition : values()) {
            if (condition.code == code) {
                return condition;
            }
        }
        throw new IllegalArgumentException("Unknown condition code: " + code);
    }

    public static Condition fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        
        String trimmed = text.trim();
        
        // Try matching by display name first
        for (Condition condition : values()) {
            if (condition.displayName.equalsIgnoreCase(trimmed)) {
                return condition;
            }
        }
        
        // Try matching by enum name (for "NEAR_MINT" style)
        try {
            return Condition.valueOf(trimmed.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Not found by name either
        }
        
        throw new IllegalArgumentException("Unknown condition: " + text);
    }
}