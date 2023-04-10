package net.villenium.skywars.handler;

import net.villenium.game.api.GameApi;
import net.villenium.game.api.item.GameItemStack;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.menu.LobbySelectorHandler;
import net.villenium.skywars.menu.MenuItemHandler;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
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
        if (Shard.getShard(e.getPlayer().getWorld().getName()) == null || !(Shard.getShard(e.getPlayer().getWorld().getName()) instanceof LobbyShard))
            return;
        if (e.getTo().getY() <= 1) {
            Location lobby = BlockUtil.strToLoc(SkyWars.getInstance().getConfig().getString("lobbyLocation"));
            lobby.setWorld(e.getPlayer().getWorld());
            e.getPlayer().teleport(lobby);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (Shard.getShard(e.getEntity().getWorld().getName()) == null || !(Shard.getShard(e.getEntity().getWorld().getName()) instanceof LobbyShard))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (Shard.getShard(e.getEntity().getWorld().getName()) == null || !(Shard.getShard(e.getEntity().getWorld().getName()) instanceof LobbyShard))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Shard.getShard(e.getPlayer().getWorld().getName()) == null || !(Shard.getShard(e.getPlayer().getWorld().getName()) instanceof LobbyShard))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (Shard.getShard(e.getWhoClicked().getWorld().getName()) == null || !(Shard.getShard(e.getWhoClicked().getWorld().getName()) instanceof LobbyShard))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (Shard.getShard(e.getPlayer().getWorld().getName()) == null || !(Shard.getShard(e.getPlayer().getWorld().getName()) instanceof LobbyShard))
            return;
        if (e.getPlayer().getLocation().getY() == 121) {
            if(e.getClickedBlock() != null && e.getClickedBlock().getState() != null) {
                if (e.getClickedBlock().getState() instanceof Sign) {
                    GamePlayer gamePlayer = GamePlayer.wrap(e.getPlayer());
                    if (!gamePlayer.isParkourAwarded()) {
                        gamePlayer.setParkourAwarded(true);
                        gamePlayer.changeCoins(1500);
                        GameApi.getUserManager().get(e.getPlayer()).getNetworkLevel().addExperience(500);
                    }
                }
            }
        }
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
        if (Shard.getShard(e.getPlayer().getWorld().getName()) == null || !(Shard.getShard(e.getPlayer().getWorld().getName()) instanceof LobbyShard))
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if (Shard.getShard(e.getPlayer().getWorld().getName()) == null || !(Shard.getShard(e.getPlayer().getWorld().getName()) instanceof LobbyShard))
            return;
        e.setCancelled(true);
    }
}
