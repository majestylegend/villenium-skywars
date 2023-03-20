package net.villenium.skywars.menu;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.skywars.player.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class MenuItemHandler {

    public MenuItemHandler(Player player) {
        Menu menu = GameApi.getMenuUtil().create("&aКомпас перемещений", 3);
        menu.addItem(new MenuButton(Material.DIAMOND_SWORD, "&bКлассика", Lists.newArrayList(
                "&7Классический и всеми любимый", "&7SkyWars без всяких ненужных", "&7наворотов ;)"
        )) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {

            }
        }, 2, 2);

        menu.open(player);
    }
}
