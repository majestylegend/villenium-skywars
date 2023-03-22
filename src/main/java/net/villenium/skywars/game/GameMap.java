package net.villenium.skywars.game;

import net.villenium.skywars.utils.BlockUtil;
import org.bukkit.Location;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GameMap {
    private final String name;
    private final List<Location> spawns;

    private final Location waitingLocation;

    public GameMap(String name, List<String> spawns, String waitingLocation) {
        this.name = name;
        this.spawns = spawns.stream().map(BlockUtil::strToLoc).collect(Collectors.toList());
        this.waitingLocation = BlockUtil.strToLoc(waitingLocation);
    }

    public Iterator<Location> getSpawnsIterator() {
        return this.spawns.iterator();
    }

    public String getName() {
        return this.name;
    }

    public List<Location> getSpawns() {
        return this.spawns;
    }

    public Location getWaitingLocation() {
        return this.waitingLocation;
    }
}