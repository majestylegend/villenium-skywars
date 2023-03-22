package net.villenium.skywars.menu;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.GameMap;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class MenuItemHandler {

    public MenuItemHandler(Player player) {
        Menu menu = GameApi.getMenuUtil().create("&aКомпас перемещений", 3);
        menu.addItem(new MenuButton(Material.DIAMOND_SWORD, "&bКлассика", Lists.newArrayList(
                "&7Классический и всеми любимый", "&7SkyWars без всяких ненужных", "&7наворотов ;)"
        )) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                if(Shard.getShard("classic") == null) {
                    new GameShard("classic", GameType.SOLO_CLASSIC, new GameMap("zimko", Arrays.asList("zimko 462.5 67.5 517.5 0 0", "zimko 462.5 67.5 483.5 0 0", "zimko 483.5 67.5 462.5 0 0", "zimko 517.5 67.5 462.5 0 0"), "zimko 0.5 62 0.5 0 0"), 3, 1);
                }
                GamePlayer.wrap(player).moveToShard(Shard.getShard("classic"));
            }
        }, 2, 2);

        menu.open(player);
    }
}
