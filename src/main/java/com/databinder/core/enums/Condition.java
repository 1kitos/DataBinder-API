package com.databinder.core.enums;

public enum Condition {

    MINT(1),
    NEAR_MINT(2),
    EXCELLENT(3),
    GOOD(4),
    LIGHT_PLAYED(5),
    PLAYED(6),
    POOR(99);

    private final int code;

    Condition(int code) {
        this.code = code;
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
}
