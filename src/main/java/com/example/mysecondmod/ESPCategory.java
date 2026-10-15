package com.example.mysecondmod;

/**
 * Категории объектов для подсветки ChestESP и их цветовое оформление (ARGB).
 */
public enum ESPCategory {
    /**
     * Обычные сундуки — оранжевый цвет (#FF8800)
     */
    CHEST(0xFFFF8800, 0x33FF8800),

    /**
     * Сундуки-ловушки — чистый красный цвет (#FF1111)
     */
    TRAPPED_CHEST(0xFFFF1111, 0x33FF1111),

    /**
     * Эндер-сундуки — голубой цвет
     */
    ENDER_CHEST(0xFF00DDFF, 0x3300DDFF),

    /**
     * Шалкеры всех цветов — фиолетовый цвет
     */
    SHULKER_BOX(0xFFCC00FF, 0x33CC00FF),

    /**
     * Вагонетки с сундуком — желтый цвет
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
