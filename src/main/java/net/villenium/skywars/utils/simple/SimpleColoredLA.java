package net.villenium.skywars.utils.simple;

import net.villenium.game.api.util.ChatUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SimpleColoredLA extends ItemStack {
    public SimpleColoredLA(final Material type, final Color color, final String name) {
        super(type, 1);
        final LeatherArmorMeta lam = (LeatherArmorMeta) this.getItemMeta();
        lam.setColor(color);
        lam.setDisplayName(ChatUtil.colorize("&f%s", new Object[]{name}));
        this.setItemMeta((ItemMeta) lam);
    }
}
