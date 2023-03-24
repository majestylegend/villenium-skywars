package net.villenium.skywars.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.usables.ActionType;
import net.villenium.skywars.game.usables.UsableItem;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSelector implements Listener {
   private static ItemStack item = new SimpleItemStack(Material.IRON_SWORD, "&cВыбор класса", Arrays.asList("&7Нажми правой кнопкой мыши,", "&7держа меня в руках, чтобы", "&7открыть меню выбора класса."));
   private static boolean initialized = false;
   private static ItemStack lobbyLeaveItem = new SimpleItemStack(Material.MAGMA_CREAM, "&4&lВыйти в лобби!");

   public static void init() {
      Bukkit.getPluginManager().registerEvents(new KitSelector(), SkyWars.getInstance());
         new UsableItem(item, ActionType.RIGHT) {
            public void onUse(Player p, ActionType actionType) {
               getClassSelector(p);
            }
         };
         new UsableItem(lobbyLeaveItem, ActionType.RIGHT) {
            public void onUse(Player p, ActionType actionType) {
               GamePlayer.wrap(p).moveToShard(Shard.getRandomLobby());
            }
         };
   }

   public static void onJoin(Player p) {
      GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
      if (game != null) {
         if (game.getGamePhase() == GamePhase.WAITING) {
            p.getInventory().setItem(0, item);
            p.getInventory().setItem(8, lobbyLeaveItem);
         }
      }
   }

   private static Menu getClassSelector(Player p) {
      GamePlayer gp = GamePlayer.wrap(p);
      Menu menu = GameApi.getMenuUtil().create("&aВыбор класса", 1 + (GameClassManager.getClasses().size() + 1) / 9);
      KitSelector.RandomClassItem rci = new KitSelector.RandomClassItem();
      GameClassManager.getClasses().forEach((gc -> {
         ItemStack icon = gc.getIcon(((GameShard)gp.getShard()).getGameType(), gp.getClassLevel(gc) == 0 ? 1 : gp.getClassLevel(gc)).clone();
         ItemMeta im = icon.getItemMeta();
         List<String> lore = im.getLore();
         if (lore == null) {
            lore = new ArrayList();
         }

         ((List)lore).add("");
         if (gp.getClassLevel(gc) == 0 && !gc.getName().equals("Builder")) {
            icon.setType(Material.STAINED_GLASS_PANE);
            icon.setDurability((short)14);
            ((List)lore).add(ChatUtil.colorize("&cУ тебя нет этого класса (купи его в лобби!)."));
         } else if (gp.getSelectedClass() == gc) {
            ((List)lore).add(ChatUtil.colorize("&2Выбран."));
         } else {
            ((List)lore).add(ChatUtil.colorize("&aНажми, чтобы выбрать."));
         }

         im.setLore((List)lore);
         icon.setItemMeta(im);
         menu.addItem(new KitSelector.ClassItem(icon.getType(), im.getDisplayName(), lore, gc, gp));
      }));

      menu.addItem(rci);

      menu.open(p);
      return menu;
   }

   private static class ClassItem extends MenuButton {
      private final GameClass gc;
      private final int level;

      public ClassItem(Material icon, String name, List<String> description, GameClass gc, GamePlayer gp) {
         super(icon, name, description);
         this.gc = gc;
         this.level = gp.getClassLevel(gc);
      }

      @Override
      public void onClick(Player player, ClickType clickType, int slot) {
         if (this.level == 0 && !this.gc.getName().equals("Builder")) {
            player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cУ тебя нет этого класса (купи его в лобби!)."));
         } else {
            GamePlayer gp = GamePlayer.wrap(player);
            gp.selectClass(this.gc);
            player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы выбрали класс &e%s&a.", this.gc.getVisibleName()));
            player.closeInventory();
         }
      }
   }

   private static class RandomClassItem extends MenuButton {
      public RandomClassItem() {
         super(Material.ITEM_FRAME, "&c??? &aСлучайный Класс &c???", Lists.newArrayList("&7Нажми для выбора случайного", "&7класса случайного уровня."));
      }

      @Override
      public void onClick(Player player, ClickType clickType, int slot) {
         GamePlayer gp = GamePlayer.wrap(player);
         gp.selectClass(null);
         player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы выбрали &cслучайный &aкласс."));
         player.closeInventory();
      }
   }
}