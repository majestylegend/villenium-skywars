package net.villenium.skywars.player;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.ScoreBoardUtil;
import net.villenium.skywars.shards.GameShard;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public class VScoreboard {

    private static final ScoreBoardUtil util = GameApi.getScoreboardUtil();
    private static final Function<GamePlayer, List<String>> lobbyScoreboardLines = player -> Lists.newArrayList(
            "&r",
            "&fУбийств: &a" + player.getSoloKills(),
            "&fПобед: &a" + player.getSoloWins(),
            "&fДуши: &b" + player.getSouls() + "&7/&b" + player.getSoulsLimit(),
            "&fСеребро: &a" + player.getCoins(),
            "&r&r",
            "     &fvillenium.net"
    );
    private static final Function<GamePlayer, List<String>> gameScoreboardLines = player -> Lists.newArrayList(
            "&r",
            "&f" + (((GameShard) player.getShard()).getPlayersPerTeam() == 1 ? "Игроков" : "Команд") + " осталось: &a" + ((GameShard) player.getShard()).getTeams().getTeamsLeft(),
            "&r&r",
            "&fУбийств: &a" + player.getKills(),
            "&r&r&r",
            "&fКарта: &a" + ((GameShard) player.getShard()).getMap().getName(),
            "&fРежим: &a" + ((GameShard) player.getShard()).getGameType().getName(),
            "&r&r&r&r",
            "     &fvillenium.net"
    );

    private static final Function<GamePlayer, List<String>> gameWaitingScoreboardLines = player -> Lists.newArrayList(
            "&r",
            "&fНеобходимо игроков: &a" + (((GameShard) player.getShard()).getPlayersMaximumAllowed() * 3 >> 3),
            "&r&r",
            "&fКарта: &a" + ((GameShard) player.getShard()).getMap().getName(),
            "&r&r&r",
            "     &fvillenium.net"
    );

    public static void setupLobbyScoreboard(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.updateTitle(player, "&3&lSkyWars");
        util.send(player, lobbyScoreboardLines.apply(gamePlayer));
    }

    public static void setupGameScoreboard(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.updateTitle(player, "&3&lSkyWars");
        util.send(player, gameScoreboardLines.apply(gamePlayer));
    }

    public static void setupGameWaitingScoreboard(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.updateTitle(player, "&3&lSkyWars");
        util.send(player, gameWaitingScoreboardLines.apply(gamePlayer));
    }

    public static void updateKills(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.send(player, 6, "&fУбийств: &a" + gamePlayer.getKills());
    }

    public static void updateTeamsLeft(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.send(player, 8, "&f" + (((GameShard) gamePlayer.getShard()).getPlayersPerTeam() == 1 ? "Игроков" : "Команд") + " осталось: &a" + ((GameShard) gamePlayer.getShard()).getTeams().getTeamsLeft());
    }

    public static void updateSilver(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.send(player, 3, "&fСеребро: &a" + gamePlayer.getCoins());
    }

    public static void updateSouls(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.send(player, 4, "&fДуши: &b" + gamePlayer.getSouls() + "&7/&b???");
    }

}