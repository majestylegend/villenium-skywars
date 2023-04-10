package net.villenium.skywars.menu;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.game.api.user.User;
import net.villenium.game.api.user.permission.PermissionGroup;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GamesHandler {

    public GamesHandler(Player player) {
        Menu menu = GameApi.getMenuUtil().create("Список игр", 5);
        Shard.getAllStartedGames().forEach((shard -> {
            GameShard gameShard = (GameShard) shard;
            Material material = gameShard.getGameType() == GameType.SOLO_CLASSIC ? Material.IRON_SWORD : gameShard.getGameType() == GameType.SOLO_INSANE ? Material.DIAMOND_SWORD : gameShard.getGameType() == GameType.TEAM_CLASSIC ? Material.IRON_HOE : Material.DIAMOND_HOE;
            menu.addItem(new MenuButton(new SimpleItemStack(material, gameShard.getMap().getVisibleName(), Arrays.asList("", "&fРежим: " + gameShard.getGameType().getName(), "&fКоманд в живых: &a" + gameShard.getTeams().getTeamsLeft(), "", "&aНажмите, чтобы смотреть игру"))) {
                @Override
                public void onClick(Player player, ClickType clickType, int slot) {
                    if(gameShard.isGameEnded() || gameShard == null) {
                        new GamesHandler(player);
                        return;
                    }
                    GamePlayer.wrap(player).moveToShard(gameShard);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(gameShard.getGamePlayers().get(new Random().nextInt(gameShard.getGamePlayers().size())).getHandle());
                }
            });
        }));
        menu.open(player);
    }

}
