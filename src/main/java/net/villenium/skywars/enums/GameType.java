package net.villenium.skywars.enums;

import net.villenium.game.api.util.ChatUtil;

public enum GameType {
    SOLO_CLASSIC(false, 1, "§bОбычный"),
    SOLO_INSANE(true, 1, "§cБезумие"),
    TEAM_CLASSIC(false, 2, "§bКомандный"),

    TEAM_INSANE(true, 2, "§cКомандное безумие");

    private final boolean insane;
    private final int playersPerTeam;
    private final String name;

    GameType(final boolean insane, final int playersPerTeam, final String name) {
        this.insane = insane;
        this.playersPerTeam = playersPerTeam;
        this.name = name;
    }

    public String getName() {
        return ChatUtil.colorize("%s%s", this.name, "");
    }

    public boolean isInsane() {
        return this.insane;
    }

    public int getPlayersPerTeam() {
        return this.playersPerTeam;
    }
}
