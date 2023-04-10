package net.villenium.skywars.enums;

import net.villenium.game.api.util.ChatUtil;

public enum Rarity {
    COMMON("&aОбычная", 16),
    RARE("&9Редкая", 8),
    EPIC("&5Эпическая", 2),
    LEGENDARY("&6Легендарная", 1);

    private final String visibleName;
    private final int copies;

    Rarity(final String visibleName, final int copies) {
        this.visibleName = ChatUtil.colorize(visibleName);
        this.copies = copies;
    }

    public String getVisibleName() {
        return this.visibleName;
    }

    public int getCopies() {
        return this.copies;
    }
}
