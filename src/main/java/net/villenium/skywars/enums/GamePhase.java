package net.villenium.skywars.enums;

import net.villenium.game.api.util.ChatUtil;

public enum GamePhase {
    WAITING("&eОжидание"),
    PREGAME("&eНачало игры"),

    INGAME("&aИгра идет"),
    ENDING("&cЗавершение"),
    RELOADING("&4Перезагрузка");

    private final String visualName;

    GamePhase(final String visualName) {
        this.visualName = ChatUtil.colorize(visualName);
    }

    public String getVisualName() {
        return this.visualName;
    }
}
