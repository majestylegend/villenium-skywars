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
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class MenuItemHandler {

    public MenuItemHandler(Player player) {
        Menu menu = GameApi.getMenuUtil().create("Компас перемещений", 3);
        int classic = Shard.getOnline(GameType.SOLO_CLASSIC) + Shard.getOnline(GameType.TEAM_CLASSIC);
        menu.addItem(new MenuButton(Material.IRON_SWORD, "&bКлассика", Lists.newArrayList(
                "&7Классический и всеми любимый", "&7SkyWars без всяких ненужных", "&7наворотов ;)", "", "&fИграет: &a" + classic + ChatUtil.transformByCount(classic, " игрок", " игрока", " игроков")
        )) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                Menu select = GameApi.getMenuUtil().create("Выбор режима", 3);
                int solo = Shard.getOnline(GameType.SOLO_CLASSIC);
                int team = Shard.getOnline(GameType.TEAM_CLASSIC);
                select.addItem(new MenuButton(Material.IRON_SWORD, "&bСоло режим", Lists.newArrayList("", "&fИграет: &a" + solo + ChatUtil.transformByCount(solo, " игрок", " игрока", " игроков"))) {
                    @Override
                    public void onClick(Player player, ClickType clickType, int slot) {
                        findGame(player, GameType.SOLO_CLASSIC);
                    }
                }, 2, 4);
                select.addItem(new MenuButton(Material.IRON_HOE, "&bКомандный режим", Lists.newArrayList("", "&fИграет: &a" + team + ChatUtil.transformByCount(team, " игрок", " игрока", " игроков"))) {
                    @Override
                    public void onClick(Player player, ClickType clickType, int slot) {
                        findGame(player, GameType.TEAM_CLASSIC);
                    }
                }, 2, 6);
                select.open(player);
            }
        }, 2, 4);

        int insane = Shard.getOnline(GameType.SOLO_INSANE) + Shard.getOnline(GameType.TEAM_INSANE);

        menu.addItem(new MenuButton(Material.DIAMOND_SWORD, "&cБезумие", Lists.newArrayList(
                "&7Классический и всеми любимый", "&7SkyWars без всяких ненужных", "&7наворотов ;)", "", "&fИграет: &a" + insane + ChatUtil.transformByCount(classic, " игрок", " игрока", " игроков")
        )) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                Menu select = GameApi.getMenuUtil().create("Выбор режима", 3);
                int solo = Shard.getOnline(GameType.SOLO_INSANE);
                int team = Shard.getOnline(GameType.TEAM_INSANE);
                select.addItem(new MenuButton(Material.DIAMOND_SWORD, "&cСоло режим", Lists.newArrayList("", "&fИграет: &a" + solo + ChatUtil.transformByCount(solo, " игрок", " игрока", " игроков"))) {
                    @Override
                    public void onClick(Player player, ClickType clickType, int slot) {
                        findGame(player, GameType.SOLO_INSANE);
                    }
                }, 2, 4);
                select.addItem(new MenuButton(Material.DIAMOND_HOE, "&cКомандный режим", Lists.newArrayList("", "&fИграет: &a" + team + ChatUtil.transformByCount(team, " игрок", " игрока", " игроков"))) {
                    @Override
                    public void onClick(Player player, ClickType clickType, int slot) {
                        findGame(player, GameType.TEAM_INSANE);
                    }
                }, 2, 6);
                select.open(player);
            }
        }, 2, 6);

        menu.addItem(new MenuButton(new SimpleItemStack(Material.ENDER_PEARL, "&eРежим наблюдателя", Arrays.asList("", "&7Нажмите, чтобы просмотреть", "&7запущенные игры"))) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                new GamesHandler(player);
            }
        }, 3, 5);

        menu.open(player);
    }


    public static void findGame(Player player, GameType gameType) {
        User user = GameApi.getUserManager().get(player);
        if (user.getParty() == null || user.getParty().getLeaderName().equals(user.getName())) {
            Shard finded = Shard.findGame(gameType, 1);
            if (finded == null) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cКарты для данного режима не установлены."));
                return;
            }
            GamePlayer.wrap(player).moveToShard(finded);
        } else {
            Shard finded = Shard.findGame(gameType, GameApi.getUserManager().get(player).getParty().getMembers().size());
            if (finded == null) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cКарты для данного режима не установлены."));
                return;
            }
            GameApi.getUserManager().get(player).getParty().getMembers().forEach((member -> {
                if (member != null && member.getSpigotPlayer() != null)
                    GamePlayer.wrap(member.getName()).moveToShard(finded);
            }));
        }
    }

}
