package net.villenium.skywars.game;

import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.AlgoUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChestsManager {
    private static final Set<Location> cached = new HashSet();

    public static void checkAndFillChest(Chest chest, Player p) {
        GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
        if (game != null) {
            Location loc = chest.getLocation();
            if (!cached.contains(loc)) {
                cached.add(loc);
                List<ItemStack> items = chest.getType() == Material.CHEST ? game.getChestGenerator().generateCommonItems(p) : game.getChestGenerator().generateCenterItems(p);
                Inventory bi = chest.getBlockInventory();
                bi.clear();
                Iterator var6 = items.iterator();

                while (var6.hasNext()) {
                    ItemStack is = (ItemStack) var6.next();

                    int slot;
                    for (slot = AlgoUtil.r(bi.getSize()); bi.getItem(slot) != null; slot = AlgoUtil.r(bi.getSize())) {
                    }

                    bi.setItem(slot, is);
                }

            }
        }
    }

    public static void reset() {
        cached.clear();
    }
}