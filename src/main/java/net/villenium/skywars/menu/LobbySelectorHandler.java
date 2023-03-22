package net.villenium.skywars.menu;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.Shard;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class LobbySelectorHandler {

    public LobbySelectorHandler(Player player) {
        Menu menu = GameApi.getMenuUtil().create("&aВыбор лобби", 1);

        Shard.getAllLobbies().forEach((lobby) -> {
            boolean playerLobby = lobby.getPlayers().contains(player);
            int number = Integer.parseInt(lobby.getId().split("-")[1]);
            menu.addItem(new MenuButton(playerLobby ? Material.MAGMA_CREAM : Material.SLIME_BALL, "&aЛобби #" + number + (playerLobby ? " &e[текущий]" : ""), Lists.newArrayList(
                    "", "&fИгроки: &b" + lobby.getPlayers().size(), "", "&aНажмите для подключения"
            )) {
                @Override
                public void onClick(Player player, ClickType clickType, int slot) {
                    if (!playerLobby) GamePlayer.wrap(player).moveToShard(lobby);
                }
            });
        });
        menu.open(player);
    }
}
