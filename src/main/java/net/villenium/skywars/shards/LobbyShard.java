package net.villenium.skywars.shards;

import lombok.Getter;
import net.villenium.skywars.handler.LobbyHandler;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Getter
public class LobbyShard extends Shard {

    public LobbyShard(String id) {
        super(id);
        WorldUtil.copyWorld("lobby", this.getId());
        this.setWorld(Bukkit.getWorld(this.getId()));
        this.getWorld().getEntities().forEach((l) -> {
            if (!(l instanceof Player)) l.remove();
        });
        this.setSleeping(false);
    }

    public void setupPlayer(Player player) {
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
        VScoreboard.setupLobbyScoreboard(gp);
    }

    protected void invalidate() {
        super.invalidate();
    }
}