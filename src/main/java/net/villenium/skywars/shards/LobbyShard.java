package net.villenium.skywars.shards;

import java.util.logging.Level;

import lombok.Getter;
import net.villenium.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class LobbyShard extends Shard {

    public LobbyShard(String id) {
        super(id);
        this.setWorld(Bukkit.getWorld(this.getId()));
        this.getWorld().getEntities().forEach((l) -> {
            if(!(l instanceof Player)) l.remove();
        });
        this.setSleeping(false);
        SkyWars.getInstance().getLogger().log(Level.INFO, "LobbyShard %d is not sleeping now!", new Object[]{this.getId()});
    }

    protected void invalidate() {
        try {
            throw new Exception("LobbyShard " + this.getId() + " is invalidating!");
        } catch (Exception var3) {
            var3.printStackTrace();
            super.invalidate();
        }
    }
}