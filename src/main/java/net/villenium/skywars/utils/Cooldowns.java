package net.villenium.skywars.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class Cooldowns implements Listener {
    private static final Map<Player, Map<String, Long>> cooldowns = new HashMap();

    public static long check(Player p, String cooldown, long secondsDelay) {
        Map<String, Long> cools = (Map) cooldowns.get(p);
        if (cools == null) {
            cools = new HashMap();
            cooldowns.put(p, cools);
        }

        long current = System.currentTimeMillis();
        Long previous = (Long) ((Map) cools).get(cooldown);
        if (previous == null) {
            previous = 0L;
        }

        if (current - previous > secondsDelay * 1000L) {
            ((Map) cools).put(cooldown, current);
            return 0L;
        } else {
            return secondsDelay - (current - previous) / 1000L;
        }
    }

    public static void uncheck(Player p, String cooldown) {
        ((Map) cooldowns.get(p)).remove(cooldown);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldowns.remove(e.getPlayer());
    }
}