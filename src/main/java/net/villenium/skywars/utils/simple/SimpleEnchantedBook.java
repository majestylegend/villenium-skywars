package net.villenium.skywars.utils.simple;

import net.villenium.game.api.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SimpleEnchantedBook extends ItemStack {
    public SimpleEnchantedBook(final Object... args) {
        super(Material.ENCHANTED_BOOK, 1);
        final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.getItemMeta();
        for (int i = 0; i < args.length; ++i) {
            if (args[i] instanceof Enchantment) {
                final int level = (int) args[i + 1];
                meta.addStoredEnchant((Enchantment) args[i++], level, true);
            } else if (args[i] instanceof Integer) {
                this.setAmount((int) args[i]);
            }
        }
        meta.setDisplayName(ChatUtil.colorize("&fКнига зачарований"));
        this.setItemMeta((ItemMeta) meta);
    }
}
