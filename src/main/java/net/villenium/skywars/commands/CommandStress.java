package net.villenium.skywars.commands;

import net.villenium.game.api.command.*;
import net.villenium.game.api.command.access.CommandAccess;
import net.villenium.game.api.command.access.CommandAccessResult;
import net.villenium.game.api.user.permission.PermissionGroup;
import net.villenium.game.api.user.permission.UserPermission;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.GameMap;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.Task;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Command("stresstest")
@Description("Проверка производительности.")
@Usage("/stresstest")
@Aliases("stress")
public class CommandStress implements CommandAccess {

    @Override
    public CommandAccessResult hasAccess(UserPermission permission) {
        return permission.isHeadAdministrator() ? null : new CommandAccessResult(PermissionGroup.HEAD_ADMIN);
    }

    @CommandHandler
    public void handle(CommandSender sender, String[] args) {
        for (int i = 0; i < 10; ++i) {
            List<GameType> gameTypeList = Arrays.asList(GameType.SOLO_CLASSIC, GameType.TEAM_CLASSIC);
            GameType gameType = gameTypeList.get(new Random().nextInt(gameTypeList.size()));
            GameMap gameMap = Shard.gameMaps.get(gameType).get(new Random().nextInt(Shard.gameMaps.get(gameType).size()));
            GameShard gameShard = new GameShard(UUID.randomUUID().toString(), gameType, gameMap, gameMap.getMaxPlayers(), gameType.getPlayersPerTeam());
            SkyWars.getInstance().getLogger().info("Shard #" + gameShard.getWorld().getName() + " (" + gameMap.getName() + ") successfully created!");
            Task.schedule(() -> {
                gameShard.switchPhase(GamePhase.PREGAME);
                SkyWars.getInstance().getLogger().info("Match of Shard #" + gameShard.getWorld().getName() + " (" + gameMap.getName() + ") successfully started!");
            }, 150L);
            Task.schedule(() -> {
                gameShard.endTheGame();
                SkyWars.getInstance().getLogger().info("Match of Shard #" + gameShard.getWorld().getName() + " (" + gameMap.getName() + ") successfully ended!");
            }, 750L);
        }
    }

}
