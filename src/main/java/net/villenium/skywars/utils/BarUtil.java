package net.villenium.skywars.utils;

import net.villenium.game.api.GameApi;
import net.villenium.game.api.Title;
import net.villenium.game.api.bar.BossBar;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import org.bukkit.entity.Player;

public class BarUtil {

    public static void updatableBar(BossBar bar, String title, int seconds, GameShard game) {
        if (Task.getTask(game.getId()) != null) {
            Task.getTask(game.getId()).cancel();
        }
        final int[] t = {seconds};
        Task var10001 = new Task(SkyWars.getInstance(), game.getId(), -1, 0, 1000) {
            public void onTick() {
                if (t[0] > 0) {
                    bar.setTitle(String.format(title, BarUtil.convertSeconds(--t[0])));
                } else {
                    bar.setTitle("");
                    this.cancel();
                }
            }
        };
    }

    public static void updatableTitle(Player player, String title, int seconds) {
        GameShard game = (GameShard) GamePlayer.wrap(player).getShard();
        if (Task.getTask(game.getId() + player.getName()) != null) {
            Task.getTask(game.getId()).cancel();
        }
        final int[] t = {seconds};
        Task var10001 = new Task(SkyWars.getInstance(), game.getId(), -1, 0, 1000) {
            public void onTick() {
                if (t[0] > 0) {
                    GameApi.getTitleManager().sendTitle(player, Title.TitleType.TITLE, String.format(title, t[0]--));
                } else {
                    this.cancel();
                }
            }
        };
    }

    public static String convertSeconds(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        String sh = (h > 0 ? h + " " + "ч" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : m + " " + "м") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + s + " " + "с");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;
    }

}
