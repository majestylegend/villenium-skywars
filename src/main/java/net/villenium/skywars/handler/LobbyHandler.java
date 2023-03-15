package net.villenium.skywars.handler;

import net.villenium.skywars.SkyWars;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class LobbyHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        GamePlayer gp = GamePlayer.wrap(p);
        VScoreboard.setupLobbyScoreboard(gp);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp() && !e.hasItem())
            e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) { e.setCancelled(true); }
}
