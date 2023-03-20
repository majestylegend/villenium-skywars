package net.villenium.skywars.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class GameMap {
   private final String name;
   private final List<Location> spawns;

   public GameMap(String name, List<String> spawns) {
      this.name = name;
      this.spawns = spawns.stream().map(GameMap::strToLoc).collect(Collectors.toList());
   }

   public static Location strToLoc(String s) {
      String[] spl = s.split(" ");
      World w = Bukkit.getWorld(spl[0]);
      if (w == null) {
         Bukkit.createWorld(new WorldCreator(spl[0]));
      }

      w = Bukkit.getWorld(spl[0]);
      double x = Double.parseDouble(spl[1]);
      double y = Double.parseDouble(spl[2]);
      double z = Double.parseDouble(spl[3]);
      float yaw = Float.parseFloat(spl[4]);
      float pitch = Float.parseFloat(spl[5]);
      Location l = new Location(w, x, y, z);
      l.setYaw(yaw);
      l.setPitch(pitch);
      return l;
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
}