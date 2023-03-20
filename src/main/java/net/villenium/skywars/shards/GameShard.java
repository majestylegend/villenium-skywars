package net.villenium.skywars.shards;

import java.util.logging.Level;

import lombok.Getter;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.GameMap;
import net.villenium.skywars.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class GameShard extends Shard {
   private GamePhase gamePhase;
   private GameType gameType;
   private int playersMaximumAllowed;
   private GameMap map;

   public GameShard(String id, GameType gameType, GameMap gameMap, int maxPlayers) {
      super(id);
      this.gamePhase = GamePhase.WAITING;
      this.playersMaximumAllowed = maxPlayers;
      this.gameType = gameType;
      this.map = gameMap;
      WorldUtil.copyWorld(map.getName(), this.getId());
      this.setWorld(Bukkit.getWorld(this.getId()));
      this.getWorld().getEntities().forEach((l) -> {
         if(!(l instanceof Player)) l.remove();
      });
      this.setSleeping(false);
      SkyWars.getInstance().getLogger().log(Level.INFO, "GameShard %d is not sleeping now!", new Object[]{this.getId()});
   }

   protected void invalidate() {
      try {
         throw new Exception("GameShard " + this.getId() + " is invalidating!");
      } catch (Exception var3) {
         var3.printStackTrace();
         super.invalidate();
      }
   }
}