package net.villenium.skywars.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.villenium.game.api.athena.annotation.Id;

import net.villenium.game.api.athena.annotation.IgnoreField;
import net.villenium.game.api.user.User;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@AllArgsConstructor
public class GamePlayer {

    @Id
    private final String name;
    private int soloKills;
    private int soloWins;
    private int coins;
    private int souls;

    @IgnoreField
    private Shard shard;

    public Player getHandle() {
        return Bukkit.getPlayerExact(name);
    }

    public static GamePlayer wrap(String name) {
        return SkyWars.getInstance().getPlayerManager().getObjectPool().get(name);
    }

    public static GamePlayer wrap(Player player) {
        return wrap(player.getName());
    }

    public static GamePlayer wrap(User user) {
        return wrap(user.getName());
    }

    public void moveToShard(Shard shard) {
        this.shard = shard;
        shard.addPlayer(this.getHandle());
        this.getHandle().teleport(shard.getWorld().getSpawnLocation());
    }

    public void moveToShard(Shard shard, Location location) {
        this.shard = shard;
        shard.addPlayer(this.getHandle());
        this.getHandle().teleport(location);
    }

    public void resetPlayer() {
        Player p = this.getHandle();
        p.setLevel(0);
        p.setExp(0.0F);
        p.getActivePotionEffects().stream().map(e -> e.getType()).forEach(p::removePotionEffect);
        if (p.getHealth() > 20.0D)
            p.setHealth(20.0D);
        p.setFireTicks(0);
        p.setMaxHealth(20.0D);
        p.setHealth(20.0D);
        p.setFoodLevel(20);
        p.setVelocity(new Vector());
    }

    public void resetPlayerInventory() {
        Player p = this.getHandle();
        p.getInventory().clear();
        p.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
    }
}