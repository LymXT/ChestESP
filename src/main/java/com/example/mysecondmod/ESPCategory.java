package com.example.mysecondmod;

/**
 * Storage object categories for ChestESP highlighting and their ARGB color configurations.
 */
public enum ESPCategory {
    /**
     * Regular chests — vibrant orange color (#FF8800).
     */
    CHEST(0xFFFF8800, 0x33FF8800),

    /**
     * Trapped chests — pure crimson red color (#FF1111).
     */
    TRAPPED_CHEST(0xFFFF1111, 0x33FF1111),

    /**
     * Ender chests — electric cyan color (#00DDFF).
     */
    ENDER_CHEST(0xFF00DDFF, 0x3300DDFF),

    /**
     * Shulker boxes of all colors — neon purple color (#CC00FF).
     */
    SHULKER_BOX(0xFFCC00FF, 0x33CC00FF),

    /**
     * Chest minecarts — bright yellow color (#FFFF00).
     */
    MINECART_CHEST(0xFFFFFF00, 0x33FFFF00);

    private final int strokeColor;
    private final int fillColor;

    ESPCategory(int strokeColor, int fillColor) {
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public int getFillColor() {
        return fillColor;
    }
}
