package net.villenium.skywars.game;

import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.Rarity;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GamePerk {
    private final String name;
    private final String visibleName;
    private final String description;
    private final Rarity rarity;
    private final int levels;
    private final Material icon;
    private final int[] costs;
    private final int[] modifiers;

    public GamePerk(String name, String visibleName, String description, Rarity rarity, Material icon, int levels, int... data) {
        this.name = name;
        this.visibleName = ChatUtil.colorize(visibleName);
        this.description = description;
        this.rarity = rarity;
        this.levels = levels;
        this.costs = new int[levels];
        this.modifiers = new int[levels];

        int i;
        for (i = 0; i < levels; ++i) {
            this.costs[i] = data[i];
        }

        for (i = levels; i < data.length; ++i) {
            this.modifiers[i - levels] = data[i];
        }

        this.icon = icon;
    }

    public ItemStack getIcon(int prelevel) {
        int level = prelevel == 0 ? 1 : prelevel;
        List<String> lore = new ArrayList();
        StringBuilder sb = new StringBuilder();
        String[] spl = this.getDescription(level).split(" ");

        for (int i = 0; i < spl.length; ++i) {
            if (!sb.toString().isEmpty()) {
                sb.append(" ");
            }

            sb.append(spl[i]);
            if (sb.length() >= 25) {
                lore.add(ChatUtil.colorize("&7%s", new Object[]{sb.toString()}));
                sb = new StringBuilder();
            }
        }

        if (!sb.toString().isEmpty()) {
            lore.add(ChatUtil.colorize("&7%s", new Object[]{sb.toString()}));
        }

        String name = this.visibleName;
        if (prelevel > 0) {
            name = name + " (Уровень " + prelevel + ")";
        }

        return new SimpleItemStack(this.icon, name, lore);
    }

    public String getDescription(int level) {
        return this.levels == 1 ? this.description : String.format(this.description, this.modifiers[level - 1]);
    }

    public int getCost(int level) {
        return this.costs[level - 1];
    }

    public int getModifier(int level) {
        return this.modifiers[level - 1];
    }

    public String getName() {
        return this.name;
    }

    public String getVisibleName() {
        return this.visibleName;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getLevels() {
        return this.levels;
    }
}