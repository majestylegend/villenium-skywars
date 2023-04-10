package net.villenium.skywars.shards;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.phantom.entity.PhantomHologram;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.handler.LobbyHandler;
import net.villenium.skywars.lobby.LobbyEngine;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.utils.BlockUtil;
import net.villenium.skywars.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter
public class LobbyShard extends Shard {

    public LobbyShard(String id) {
        super(id);
        WorldUtil.cloneWorld("lobby", this.getId());
        this.setWorld(Bukkit.getWorld(this.getId()));
        this.getWorld().getEntities().forEach((l) -> {
            if (!(l instanceof Player)) l.remove();
        });
        this.getWorld().setGameRuleValue("announceAdvancements", "false");
        this.setSleeping(false);
        Location loc = new Location(this.getWorld(), -15.5, 101, -16.5);
        loc.setY(loc.getY() - 0.5D);
        PhantomHologram first = GameApi.getPhantomEntityFactory().createHologram("&b&lКолодец Душ");
        first.setLocation(loc);
        first.spawn(true);
        loc.setY(loc.getY() - 0.25D);
        PhantomHologram second = GameApi.getPhantomEntityFactory().createHologram("&e&lНАЖМИ НА МЕНЯ");
        second.setLocation(loc);
        second.spawn(true);
    }

    public void setupPlayer(Player player) {
        Location lobby = BlockUtil.strToLoc(SkyWars.getInstance().getConfig().getString("lobbyLocation"));
        lobby.setWorld(this.getWorld());
        player.teleport(lobby);
        GamePlayer gp = GamePlayer.wrap(player);
        gp.setKills(0);
        gp.setAssists(0);
        gp.setIncreasedDamage(0);
        gp.setPlayed(false);
        gp.setWraithInfometer(null);
        gp.setDoomedUntil(0);
        gp.resetPlayer();
        gp.resetPlayerInventory();
        gp.getHandle().setGameMode(GameMode.SURVIVAL);
        player.getInventory().setItem(0, LobbyHandler.item);
        player.getInventory().setItem(8, LobbyHandler.lobby);
        player.getInventory().setItem(4, LobbyEngine.item);
        VScoreboard.setupLobbyScoreboard(gp);
        if (GameApi.getUserManager().get(player).getPermission().isPremium()) {
            player.setAllowFlight(true);
            player.setFlying(false);
        } else {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        player.setPlayerListName(GameApi.getUserManager().get(player).getFullDisplayName());
    }

    protected void invalidate() {
        super.invalidate();
    }
}