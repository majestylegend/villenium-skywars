package net.villenium.skywars.game;

import net.villenium.game.api.item.GameItemStack;
import net.villenium.game.api.item.GameItemStackMetaBuilder;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.Rarity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

public class Cage {
    private final String name;
    private final String visualName;
    private final Rarity rarity;
    private final MaterialData[] datas;
    private final ItemStack icon;

    public Cage(final String name, final String visualName, final Rarity rarity, final MaterialData icon, final MaterialData... blocks) {
        this.name = name;
        this.visualName = ChatUtil.colorize(visualName);
        this.rarity = rarity;
        this.datas = blocks;
        this.icon = this.getIcon(icon);
    }

    public Cage(final String name, final String visualName, final Rarity rarity, final MaterialData singleData) {
        this.name = name;
        this.visualName = ChatUtil.colorize(visualName);
        this.rarity = rarity;
        this.datas = new MaterialData[36];
        for (int i = 0; i < this.datas.length; ++i) {
            this.datas[i] = singleData;
        }
        this.icon = this.getIcon(singleData);
    }

    public static void clear(final Location spawnPoint) {
        final World w = spawnPoint.getWorld();
        final int X = spawnPoint.getBlockX();
        final int Y = spawnPoint.getBlockY();
        final int Z = spawnPoint.getBlockZ();
        for (int x = X - 1; x <= X + 1; ++x) {
            for (int z = Z - 1; z <= Z + 1; ++z) {
                for (int y = Y + 3; y >= Y; --y) {
                    w.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    private ItemStack getIcon(final MaterialData data) {
        final String s = ChatUtil.colorize(this.visualName);
        return new GameItemStack(data.getItemType(), this.visualName, Arrays.asList("&7В начале каждой новой игры вы", "&7будете появляться не в обычной", "&7стеклянной клетке. Теперь для", "&7этой цели будет использоваться", "&7" + s + ", делая вас", "&7по-настоящему неотразимым и", "&7выделяющимся из толпы!"), new GameItemStackMetaBuilder().materialData(data.getData()).build());
    }

    public void build(final Location spawnPoint) {
        final World w = spawnPoint.getWorld();
        final int X = spawnPoint.getBlockX();
        final int Y = spawnPoint.getBlockY();
        final int Z = spawnPoint.getBlockZ();
        int current = 0;
        for (int y = Y + 3; y >= Y; --y) {
            for (int x = X - 1; x <= X + 1; ++x) {
                for (int z = Z - 1; z <= Z + 1; ++z) {
                    final MaterialData data = this.datas[current++];
                    if (x == X && z == Z) {
                        if (y == Y + 1) {
                            continue;
                        }
                        if (y == Y + 2) {
                            continue;
                        }
                    }
                    final Block block = w.getBlockAt(x, y, z);
                    block.setType(data.getItemType());
                    block.setData(data.getData());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Cage::" + this.name;
    }

    public String getName() {
        return this.name;
    }

    public String getVisualName() {
        return this.visualName;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public MaterialData[] getDatas() {
        return this.datas;
    }

    public ItemStack getIcon() {
        return this.icon;
    }
}
