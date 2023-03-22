package net.villenium.skywars.game;

import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class GameClass {
    private final String name;
    private final String visibleName;
    private final Material icon;
    private final Map<GameType, GameClass.ClassInfo[]> info = new HashMap();

    public GameClass(String name, String visibleName, Material icon) {
        this.name = name;
        this.visibleName = ChatUtil.colorize(visibleName);
        this.icon = icon;
    }

    public static String getLevel(int level) {
        switch (level) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return String.valueOf(level);
        }
    }

    private static String getEnchantName(String name) {
        name = name.replace("_", " ");
        String[] args = name.split(" ");

        StringBuilder sb;
        for (int i = 0; i < args.length; ++i) {
            sb = new StringBuilder();
            sb.append(args[i].toLowerCase());
            sb.setCharAt(0, (char) (sb.charAt(0) - 97 + 65));
            args[i] = sb.toString();
        }

        sb = new StringBuilder();
        String[] var7 = args;
        int var4 = args.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String s = var7[var5];
            sb.append(s).append(" ");
        }

        return sb.toString();
    }

    public GameClass expand(GameType type, GameClass.ClassInfo... info) {
        GameClass.ClassInfo[] var3 = info;
        int var4 = info.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            GameClass.ClassInfo ci = var3[var5];
            ci.setupIcon(this.icon, this.visibleName);
        }

        this.info.put(type, info);
        return this;
    }

    public GameClass expand(GameType type, GameType original) {
        this.info.put(type, this.info.get(original));
        return this;
    }

    public ItemStack getIcon(GameType type, int level) {
        return ((GameClass.ClassInfo[]) this.info.get(type))[level - 1].icon;
    }

    public void setup(Player p, int level) {
        if (!(GamePlayer.wrap(p).getShard() instanceof GameShard)) return;
        GameClass.ClassInfo[] infos = (GameClass.ClassInfo[]) this.info.get(((GameShard) GamePlayer.wrap(p).getShard()).getGameType());
        if (infos != null) {
            GameClass.ClassInfo info = infos[level - 1];
            if (info != null) {
                p.getInventory().addItem(info.getItems());
            }
        }

    }

    public int getLevels() {
        GameClass.ClassInfo[] info = (GameClass.ClassInfo[]) this.info.get(GameType.SOLO_CLASSIC);
        if (info == null) {
            throw new IllegalStateException("Can not get levels for GameClass " + this.name + "(" + this.info.toString() + ")");
        } else {
            return info.length;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getVisibleName() {
        return this.visibleName;
    }

    public static class ClassInfo {
        private final int level;
        private final ItemStack[] items;
        private final List<String> description;
        private ItemStack icon;

        public ClassInfo(int level, ItemStack... items) {
            this.level = level;
            this.items = items;
            this.description = new ArrayList();
            ItemStack[] var3 = items;
            int var4 = items.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                ItemStack is = var3[var5];
                String msg;
                StringBuilder sb;
                Iterator var10;
                Enchantment enchant;
                String result;
                if (is.getType() == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
                    sb = new StringBuilder();
                    var10 = meta.getStoredEnchants().keySet().iterator();

                    while (var10.hasNext()) {
                        enchant = (Enchantment) var10.next();
                        sb.append(GameClass.getEnchantName(enchant.getName())).append(meta.getStoredEnchants().get(enchant)).append(", ");
                    }

                    result = sb.toString();
                    result = result.substring(0, result.length() - 2);
                    msg = ChatUtil.colorize("&8- &r%s &7(&e%s&7)", new Object[]{is.getItemMeta().getDisplayName(), result});
                } else if (!is.getItemMeta().hasEnchants()) {
                    msg = ChatUtil.colorize("&8- &r%s&r%s", new Object[]{is.getItemMeta().getDisplayName(), is.getAmount() == 1 ? "" : " x" + is.getAmount()});
                } else {
                    ItemMeta im = is.getItemMeta();
                    sb = new StringBuilder();
                    var10 = im.getEnchants().keySet().iterator();

                    while (var10.hasNext()) {
                        enchant = (Enchantment) var10.next();
                        sb.append(GameClass.getEnchantName(enchant.getName())).append(im.getEnchants().get(enchant)).append(", ");
                    }

                    result = sb.toString();
                    result = result.substring(0, result.length() - 2);
                    msg = ChatUtil.colorize("&8- &r%s &7(&e%s&7)", new Object[]{is.getItemMeta().getDisplayName(), result});
                }

                this.description.add(msg);
            }

        }

        public ClassInfo(int level, List<String> description, ItemStack... items) {
            this.level = level;
            this.items = items;
            this.description = (List) description.stream().map(ChatUtil::colorize).collect(Collectors.toList());
        }

        private void setupIcon(Material icon, String visibleName) {
            this.icon = new SimpleItemStack(icon, visibleName + " " + GameClass.getLevel(this.level), this.description);
        }

        public ItemStack getIcon() {
            return this.icon;
        }

        public ItemStack[] getItems() {
            return this.items;
        }
    }
}