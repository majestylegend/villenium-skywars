package net.villenium.skywars.handler;

import net.villenium.game.api.item.GameItemStack;
import net.villenium.skywars.menu.LobbySelectorHandler;
import net.villenium.skywars.menu.MenuItemHandler;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class LobbyHandler implements Listener {

    public static ItemStack item = new GameItemStack(Material.COMPASS, "&aКомпас пермещений");
    public static ItemStack lobby = new GameItemStack(Material.SLIME_BALL, "&aВыбор лобби");

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        GamePlayer gp = GamePlayer.wrap(p);
        gp.resetPlayer();
        gp.resetPlayerInventory();
        gp.moveToShard(Shard.getRandomLobby());
        VScoreboard.setupLobbyScoreboard(gp);
        p.getInventory().setItem(0, item);
        p.getInventory().setItem(8, lobby);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().getWorld().getName().contains("lobby")) return;
        if (e.getTo().getY() <= 1) {
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!e.getEntity().getWorld().getName().contains("lobby")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().contains("lobby")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getWorld().getName().contains("lobby")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getWhoClicked().getWorld().getName().contains("lobby")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().contains("lobby")) return;
        if (!e.getPlayer().isOp() && !e.hasItem()) {
            e.setCancelled(true);
        } else if (e.hasItem() && e.getItem().getType() == Material.COMPASS) {
            new MenuItemHandler(e.getPlayer());
        } else if (e.hasItem() && e.getItem().getType() == Material.SLIME_BALL) {
            new LobbySelectorHandler(e.getPlayer());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!e.getPlayer().getWorld().getName().contains("lobby")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (!e.getPlayer().getWorld().getName().contains("lobby")) return;
        e.setCancelled(true);
    }
}
