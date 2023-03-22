package net.villenium.skywars.utils.simple;

import net.villenium.game.api.util.ChatUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleItemStack extends ItemStack {
    public SimpleItemStack(Material m, String name, String desc, Object... datas) {
        this(m, 1, name, desc, datas);
    }

    public SimpleItemStack(Material m, String name, List<String> desc, Object... datas) {
        this(m, 1, name, desc, datas);
    }

    public SimpleItemStack(PotionType pt, int level, boolean extended_duration, boolean splash, int amount, String name) {
        this(Material.POTION, amount, name, "", new Object[]{Short.valueOf(getPotionType(pt, level, extended_duration, splash))});
    }

    public SimpleItemStack(Material m, int amount, String name, List<String> desc, Object... datas) {
        super(m, amount);
        ItemMeta im = getItemMeta();
        im.setDisplayName(ChatUtil.colorize("&e%s", new Object[]{name}));
        setItemMeta(im);
        if (desc != null && !desc.isEmpty()) {
            im.setLore((List) desc.stream().map(ChatUtil::colorize).collect(Collectors.toList()));
            setItemMeta(im);
        }
        if (datas == null || datas.length == 0)
            return;
        for (int i = 0; i < datas.length; i++) {
            Object data = datas[i];
            if (data instanceof Color) {
                try {
                    LeatherArmorMeta lam = (LeatherArmorMeta) im;
                    lam.setColor((Color) data);
                    setItemMeta((ItemMeta) lam);
                } catch (Exception exception) {
                }
            } else if (data instanceof Enchantment && datas[i + 1] instanceof Integer) {
                addUnsafeEnchantment((Enchantment) data, ((Integer) datas[i + 1]).intValue());
                i++;
            } else if (data instanceof Integer) {
                setAmount(((Integer) data).intValue());
            } else if (data instanceof Short) {
                setDurability(((Short) data).shortValue());
            }
        }
    }

    public SimpleItemStack(Material m, int amount, String name, String desc, Object... datas) {
        this(m, amount, name, descStringToList(desc), datas);
    }

    public SimpleItemStack(Material m, int amount, String name) {
        this(m, amount, name, "", (Object[]) null);
    }

    public SimpleItemStack(Material m, String name) {
        this(m, name, "");
    }

    public SimpleItemStack(Material m, String name, List<String> desc) {
        this(m, name, desc, (Object[]) null);
    }

    public SimpleItemStack(Material m, String name, String desc) {
        this(m, name, desc, (Object[]) null);
    }

    public SimpleItemStack(Material m, String name, Object... objects) {
        this(m, name, "", objects);
    }

    private static short getPotionType(PotionType pt, int level, boolean extended_duration, boolean splash) {
        Potion pot = new Potion(pt);
        pot.setLevel(level);
        pot.setSplash(splash);
        if (extended_duration)
            try {
                pot.setHasExtendedDuration(extended_duration);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        return pot.toItemStack(1).getDurability();
    }

    private static List<String> descStringToList(String desc) {
        List<String> list = new ArrayList<>();
        if (desc == null || desc.isEmpty())
            return list;
        list.addAll(Arrays.asList(desc.split("\\|")));
        return list;
    }

    public SimpleItemStack applyFlags(ItemFlag... flags) {
        ItemMeta im = getItemMeta();
        im.addItemFlags(flags);
        setItemMeta(im);
        return this;
    }

    public SimpleItemStack setUnbreakable(boolean value) {
        ItemMeta im = getItemMeta();
        im.spigot().setUnbreakable(value);
        setItemMeta(im);
        return this;
    }
}
