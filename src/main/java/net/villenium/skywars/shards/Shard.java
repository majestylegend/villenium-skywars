package net.villenium.skywars.shards;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Shard {
   private static final Map<String, Shard> shards = new ConcurrentHashMap();
   private static final Map<String, String> waitingFor = new HashMap();
   private static final Set<Shard> pendingRestoring = new HashSet();
   private String id;
   private World world;
   private final Collection<Player> players = new HashSet();
   private boolean sleeping = true;

   public static Shard getShard(String id) {
      return (Shard)shards.get(id);
   }

   public static void waitPlayers(Collection<String> players, String shardId) {
      players.forEach((p) -> {
         waitingFor.put(p.toLowerCase(), shardId);
      });
   }

   private static void toLobby(Player p, String msg) {
      p.sendMessage(ChatUtil.colorize(msg));
      Task.schedule(() -> {
         GamePlayer.wrap(p).moveToShard(shards.get("lobby"));
      }, 5L);
   }

   public static void processQuitEvent(Player p) {
      Shard shard = (Shard)shards.remove(p.getName());
      if (shard != null) {
         shard.players.remove(p);
      }

   }

   public static void invalidate(Shard shard, boolean forcefully) {
      if (forcefully) {
         shard.invalidate();
      } else {
         pendingRestoring.add(shard);
         if (shards.values().stream().allMatch(Shard::isSleeping)) {
            pendingRestoring.forEach((gs) -> {
               gs.invalidate();
            });
            pendingRestoring.clear();
         }
      }
   }

   public Shard(String id) {
      this.id = id;
      shards.put(id, this);
   }

   public final void setWorld(World world) {
      this.world = world;
   }

   public final void setWorldByName(String world) {
      if(Bukkit.getWorld(world) != null) {
         this.world = Bukkit.getWorld(world);
      }
   }

   public final void addPlayer(Player p) {
      this.players.add(p);
      shards.put(p.getName(), this);
   }

   protected void invalidate() {
      shards.remove(this.getId());
      WorldUtil.deleteWorld(world);
      this.id = null;
      this.world = null;
      this.players.clear();
   }
}