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

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = GamePlayer.wrap(p);
        gp.resetPlayer();
        gp.resetPlayerInventory();
        p.teleport(((World) Bukkit.getWorlds().get(0)).getSpawnLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        SkyWars.getInstance().getPlayerManager().getObjectPool().save(e.getPlayer().getName(), true);
    }
}
