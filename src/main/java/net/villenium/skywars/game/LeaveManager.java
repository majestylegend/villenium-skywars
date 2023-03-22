package net.villenium.skywars.game;

import net.villenium.skywars.player.GamePlayer;

public class LeaveManager {

    public static boolean canLogin(String player) {
        return System.currentTimeMillis() - GamePlayer.wrap(player).getLeave() > 180000L;
    }

    public static void addLeave(String player) {
        GamePlayer.wrap(player).setLeave(System.currentTimeMillis());
    }
}