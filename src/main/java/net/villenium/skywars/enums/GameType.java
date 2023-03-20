package net.villenium.skywars.enums;

import net.villenium.game.api.util.ChatUtil;

public enum GameType
{
  SOLO_CLASSIC(false, 1, "Обычный"),
  SOLO_INSANE(true, 1, "Безумие"),
  TEAM_CLASSIC(false, 2, "Командный"),
  TEAM_INSANE(true, 2, "Командное безумие");

  private final boolean insane;
  private final int playersPerTeam;
  private final String name;

  public String getName() {
    return ChatUtil.colorize("%s%s", new Object[] { this.name, "" });
  }

  private GameType(final boolean insane, final int playersPerTeam, final String name) {
    this.insane = insane;
    this.playersPerTeam = playersPerTeam;
    this.name = name;
  }

  public boolean isInsane() {
    return this.insane;
  }

  public int getPlayersPerTeam() {
    return this.playersPerTeam;
  }
}
