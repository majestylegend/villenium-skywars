package net.villenium.skywars.handler;

import net.villenium.skywars.SkyWars;
import net.villenium.skywars.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlobalHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        SkyWars.getInstance().getPlayerManager().getObjectPool().save(e.getPlayer().getName(), true);
    }
}
