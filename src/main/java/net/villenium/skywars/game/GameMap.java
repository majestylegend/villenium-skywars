package net.villenium.skywars.game;

import net.villenium.skywars.utils.BlockUtil;
import org.bukkit.Location;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GameMap {
    private final String name;
    private final String visibleName;
    private final List<Location> spawns;
    private final List<Location> deathMatch;
    private final Location waitingLocation;
    private final int maxPlayers;

    public GameMap(String name, String visibleName, List<String> spawns, List<String> deathMatch, String waitingLocation, int maxPlayers) {
        this.name = name;
        this.visibleName = visibleName;
        this.spawns = spawns.stream().map(BlockUtil::strToLoc).collect(Collectors.toList());
        this.deathMatch = deathMatch.stream().map(BlockUtil::strToLoc).collect(Collectors.toList());
        this.waitingLocation = BlockUtil.strToLoc(waitingLocation);
        this.maxPlayers = maxPlayers;
    }

    public Iterator<Location> getSpawnsIterator() {
        return this.spawns.iterator();
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getVisibleName() {
        return this.visibleName;
    }

    public List<Location> getSpawns() {
        return this.spawns;
    }

    public Iterator<Location> getDeathMatchIterator() {
        return this.deathMatch.iterator();
    }

    public Location getWaitingLocation() {
        return this.waitingLocation;
    }
}