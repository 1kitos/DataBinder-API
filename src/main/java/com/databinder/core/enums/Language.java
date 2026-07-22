package com.databinder.core.enums;

public enum Language 
{
	ALL(0),
	EN(1),
	FR(2),
	DE(3),
	ES(4),
	IT(5),
	CH(6),
	JP(7),
	PT(8);
	
	
	private final int code;

    Language(int code) {
        this.code = code;
    }
    
    
    public int getCode() {
        return code;
    }

    public static Language fromCode(int code) {
        for (Language language : values()) {
            if (language.code == code) {
                return language;
            }
        }

        throw new IllegalArgumentException("Unknown language code: " + code);
    }
	
	
}
