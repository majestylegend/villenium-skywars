package net.villenium.skywars.utils;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import net.villenium.game.api.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class UtilItem {
    public static void setDisplayName(ItemStack is, String name) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.setDisplayName(ChatUtil.colorize(name));
                is.setItemMeta(im);
            }
        }
    }

    public static void changeDisplayName(ItemStack is, String name) {
        setDisplayName(is, name);
    }

    public static String getDisplayName(ItemStack is) {
        if (is == null) {
            return null;
        } else {
            ItemMeta im = is.getItemMeta();
            return im == null ? null : im.getDisplayName();
        }
    }

    public static List<String> getLore(ItemStack is) {
        if (is == null) {
            return null;
        } else {
            ItemMeta im = is.getItemMeta();
            return im == null ? null : im.getLore();
        }
    }

    public static List<String> getDescription(ItemStack is) {
        return getLore(is);
    }

    public static void setDescription(ItemStack is, List<String> description) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.setLore((List) description.stream().map(ChatUtil::colorize).collect(Collectors.toList()));
                is.setItemMeta(im);
            }
        }
    }

    public static void setDescription(ItemStack is, String... description) {
        setDescription(is, AlgoUtil.newArrayList(description));
    }

    public static void addDescriptionAfter(ItemStack is, List<String> description) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                List<String> lore = im.getLore();
                if (lore == null) {
                    lore = new ArrayList();
                }

                List<String> finalLore = new ArrayList();
                finalLore.addAll((Collection) lore);
                finalLore.addAll((Collection) description.stream().map(ChatUtil::colorize).collect(Collectors.toList()));
                im.setLore(finalLore);
                is.setItemMeta(im);
            }
        }
    }

    public static void addDescriptionAfter(ItemStack is, String... description) {
        addDescriptionAfter(is, AlgoUtil.newArrayList(description));
    }

    public static void addDescriptionBefore(ItemStack is, List<String> description) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                List<String> lore = im.getLore();
                if (lore == null) {
                    lore = new ArrayList();
                }

                List<String> finalLore = new ArrayList();
                finalLore.addAll((Collection) description.stream().map(ChatUtil::colorize).collect(Collectors.toList()));
                finalLore.addAll((Collection) lore);
                im.setLore(finalLore);
                is.setItemMeta(im);
            }
        }
    }

    public static void addDescriptionBefore(ItemStack is, String... description) {
        addDescriptionBefore(is, AlgoUtil.newArrayList(description));
    }

    public static void addEnchantment(ItemStack is, Enchantment e) {
        addEnchantment(is, e, 10);
    }

    public static void addEnchantment(ItemStack is, Enchantment e, int level) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.addEnchant(e, level, true);
                is.setItemMeta(im);
            }
        }
    }

    public static void hideAllFlafs(ItemStack is) {
        addItemFlags(is, ItemFlag.values());
    }

    public static void addItemFlags(ItemStack is, ItemFlag... flags) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.addItemFlags(flags);
                is.setItemMeta(im);
            }
        }
    }

    public static boolean isHelmet(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.LEATHER_HELMET || type == Material.CHAINMAIL_HELMET || type == Material.GOLD_HELMET || type == Material.IRON_HELMET || type == Material.DIAMOND_HELMET;
        }
    }

    public static boolean isChestplate(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.LEATHER_CHESTPLATE || type == Material.CHAINMAIL_CHESTPLATE || type == Material.GOLD_CHESTPLATE || type == Material.IRON_CHESTPLATE || type == Material.DIAMOND_CHESTPLATE;
        }
    }

    public static boolean isLeggings(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.LEATHER_LEGGINGS || type == Material.CHAINMAIL_LEGGINGS || type == Material.GOLD_LEGGINGS || type == Material.IRON_LEGGINGS || type == Material.DIAMOND_LEGGINGS;
        }
    }

    public static boolean isBoots(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.LEATHER_BOOTS || type == Material.CHAINMAIL_BOOTS || type == Material.GOLD_BOOTS || type == Material.IRON_BOOTS || type == Material.DIAMOND_BOOTS;
        }
    }

    public static boolean isHoe(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.WOOD_HOE || type == Material.STONE_HOE || type == Material.IRON_HOE || type == Material.GOLD_HOE || type == Material.DIAMOND_HOE;
        }
    }

    public static boolean isPickaxe(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.WOOD_PICKAXE || type == Material.STONE_PICKAXE || type == Material.IRON_PICKAXE || type == Material.GOLD_PICKAXE || type == Material.DIAMOND_PICKAXE;
        }
    }

    public static boolean isSpade(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.WOOD_SPADE || type == Material.STONE_SPADE || type == Material.IRON_SPADE || type == Material.GOLD_SPADE || type == Material.DIAMOND_SPADE;
        }
    }

    public static boolean isShovel(ItemStack is) {
        return isSpade(is);
    }

    public static boolean isAxe(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.WOOD_AXE || type == Material.STONE_AXE || type == Material.IRON_AXE || type == Material.GOLD_AXE || type == Material.DIAMOND_AXE;
        }
    }

    public static boolean isSword(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.WOOD_SWORD || type == Material.STONE_SWORD || type == Material.IRON_SWORD || type == Material.GOLD_SWORD || type == Material.DIAMOND_SWORD;
        }
    }

    public static boolean isBow(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            return is.getType() == Material.BOW;
        }
    }

    public static boolean isWeapon(ItemStack is) {
        return isSword(is) || isBow(is);
    }

    public static boolean isArmor(ItemStack is) {
        return isHelmet(is) || isChestplate(is) || isLeggings(is) || isBoots(is);
    }

    public static boolean isLeather(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_BOOTS;
        }
    }

    public static boolean isStone(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.STONE_SWORD || type == Material.STONE_HOE || type == Material.STONE_PICKAXE || type == Material.STONE_SPADE || type == Material.STONE_AXE;
        }
    }

    public static boolean isWood(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.WOOD_SWORD || type == Material.WOOD_HOE || type == Material.WOOD_PICKAXE || type == Material.WOOD_SPADE || type == Material.WOOD_AXE;
        }
    }

    public static boolean isChainmail(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.CHAINMAIL_HELMET || type == Material.CHAINMAIL_LEGGINGS || type == Material.CHAINMAIL_CHESTPLATE || type == Material.CHAINMAIL_BOOTS;
        }
    }

    public static boolean isGold(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.GOLD_HELMET || type == Material.GOLD_LEGGINGS || type == Material.GOLD_CHESTPLATE || type == Material.GOLD_BOOTS || type == Material.GOLD_SWORD || type == Material.GOLD_HOE || type == Material.GOLD_PICKAXE || type == Material.GOLD_SPADE || type == Material.GOLD_AXE;
        }
    }

    public static boolean isIron(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.IRON_HELMET || type == Material.IRON_LEGGINGS || type == Material.IRON_CHESTPLATE || type == Material.IRON_BOOTS || type == Material.IRON_SWORD || type == Material.IRON_HOE || type == Material.IRON_PICKAXE || type == Material.IRON_SPADE || type == Material.IRON_AXE;
        }
    }

    public static boolean isDiamond(ItemStack is) {
        if (is == null) {
            return false;
        } else {
            Material type = is.getType();
            return type == Material.DIAMOND_HELMET || type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_CHESTPLATE || type == Material.DIAMOND_BOOTS || type == Material.DIAMOND_SWORD || type == Material.DIAMOND_HOE || type == Material.DIAMOND_PICKAXE || type == Material.DIAMOND_SPADE || type == Material.DIAMOND_AXE;
        }
    }

    public static void setUnbreakable(ItemStack is, boolean value) {
        if (is != null) {
            ItemMeta im = is.getItemMeta();
            if (im != null) {
                im.spigot().setUnbreakable(value);
                is.setItemMeta(im);
            }
        }
    }

    private static Enchantment isEnchantment(Enchantment[] values, String name) {
        Enchantment[] var2 = values;
        int var3 = values.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Enchantment e = var2[var4];
            if (e.getName().toLowerCase().equals(name)) {
                return e;
            }
        }

        return null;
    }

    private static PotionEffectType isPotionEffectType(PotionEffectType[] values, String name) {
        PotionEffectType[] var2 = values;
        int var3 = values.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            PotionEffectType p = var2[var4];
            if (p != null && p.getName().toLowerCase().equals(name)) {
                return p;
            }
        }

        return null;
    }

    public static ItemStack glow(ItemStack is) {
        return nbt(is).set("ench", new NBTTagList()).build();
    }


    private static UtilItem.NBT nbt(ItemStack is) {
        return new UtilItem.NBT(is);
    }

    public static class NBT {
        private final net.minecraft.server.v1_12_R1.ItemStack nms;

        private NBT(ItemStack is) {
            this.nms = CraftItemStack.asNMSCopy(is);
            if (!this.nms.hasTag()) {
                this.nms.setTag(new NBTTagCompound());
            }

        }

        // $FF: synthetic method
        NBT(ItemStack x0, Object x1) {
            this(x0);
        }

        private NBTTagCompound getTag(String path, boolean write) {
            if (path.contains(".")) {
                String[] parts = path.split("\\.");
                NBTTagCompound t = this.nms.getTag();

                for (int i = 0; i < parts.length - 1; ++i) {
                    NBTTagCompound t0 = t.getCompound(parts[i]);
                    if (write && t.hasKey(parts[i])) {
                        t.set(parts[i], t0);
                    }

                    t = t0;
                }

                return t;
            } else {
                return this.nms.getTag();
            }
        }

        private String getKey(String path) {
            int index = path.lastIndexOf(46);
            return index == -1 ? path : path.substring(index + 1);
        }

        public UtilItem.NBT set(String path, NBTBase val) {
            this.getTag(path, true).set(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setByte(String path, byte val) {
            this.getTag(path, true).setByte(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setByteArray(String path, byte[] val) {
            this.getTag(path, true).setByteArray(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setBoolean(String path, boolean val) {
            this.getTag(path, true).setBoolean(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setDouble(String path, double val) {
            this.getTag(path, true).setDouble(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setFloat(String path, float val) {
            this.getTag(path, true).setFloat(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setInt(String path, int val) {
            this.getTag(path, true).setInt(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setIntArray(String path, int[] val) {
            this.getTag(path, true).setIntArray(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setLong(String path, long val) {
            this.getTag(path, true).setLong(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setShort(String path, short val) {
            this.getTag(path, true).setShort(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setString(String path, String val) {
            this.getTag(path, true).setString(this.getKey(path), val);
            return this;
        }

        public UtilItem.NBT setStringList(String path, List<String> val) {
            String key = this.getKey(path);
            NBTTagList nbtList = new NBTTagList();
            Iterator var5 = val.iterator();

            while (var5.hasNext()) {
                String s = (String) var5.next();
                nbtList.add(new NBTTagString(s));
            }

            this.getTag(path, true).set(key, nbtList);
            return this;
        }

        public NBTBase get(String path) {
            return this.getTag(path, false).get(this.getKey(path));
        }

        public byte getByte(String path) {
            return this.getTag(path, false).getByte(this.getKey(path));
        }

        public byte[] getByteArray(String path) {
            return this.getTag(path, false).getByteArray(this.getKey(path));
        }

        public boolean getBoolean(String path) {
            return this.getTag(path, false).getBoolean(this.getKey(path));
        }

        public double getDouble(String path) {
            return this.getTag(path, false).getDouble(this.getKey(path));
        }

        public float getFloat(String path) {
            return this.getTag(path, false).getFloat(this.getKey(path));
        }

        public int getInt(String path) {
            return this.getTag(path, false).getInt(this.getKey(path));
        }

        public int[] getIntArray(String path) {
            return this.getTag(path, false).getIntArray(this.getKey(path));
        }

        public long getLong(String path) {
            return this.getTag(path, false).getLong(this.getKey(path));
        }

        public short getShort(String path) {
            return this.getTag(path, false).getShort(this.getKey(path));
        }

        public String getString(String path) {
            return this.getTag(path, false).getString(this.getKey(path));
        }

        public UtilItem.NBT remove(String path) {
            this.getTag(path, false).remove(this.getKey(path));
            return this;
        }

        public boolean contains(String path) {
            return this.getTag(path, false).hasKey(this.getKey(path));
        }

        public NBTTagCompound getHandle() {
            return this.nms.getTag();
        }

        public ItemStack build() {
            return CraftItemStack.asCraftMirror(this.nms);
        }
    }

    public static class CantParseItemException extends Exception {
        private CantParseItemException(Throwable cause) {
            super(cause);
        }

        // $FF: synthetic method
        CantParseItemException(Throwable x0, Object x1) {
            this(x0);
        }
    }

    private static class PotionCraft {
        private PotionEffectType type;
        private int power;
        private int duration;

        private PotionCraft() {
        }

        // $FF: synthetic method
        PotionCraft(Object x0) {
            this();
        }
    }
}