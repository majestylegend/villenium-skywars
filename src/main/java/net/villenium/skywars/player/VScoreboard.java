package net.villenium.skywars.player;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.ScoreBoardUtil;
import net.villenium.skywars.SkyWars;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public class VScoreboard {

    private static final ScoreBoardUtil util = GameApi.getScoreboardUtil();

    static {
        util.enableTitleAnimationGamma("SkyWars", ScoreBoardUtil.AnimationGamma.AQUA);
    }

    private static final Function<GamePlayer, List<String>> lobbyScoreboardLines = player -> Lists.newArrayList(
            "",
            "&fУбийств: &a" + player.getSoloKills(),
            "&fПобед: &a" + player.getSoloWins(),
            "&fДуши: &b" + player.getSouls() + "&7/&b???",
            "&fСеребро: &a" + player.getCoins(),
            "",
            "     &fwww.villenium.net"
    );

    /*private static final Function<GamePlayer, List<String>> gameScoreboardLines = player -> Lists.newArrayList(
            "",
            "&f" + (SkyWars.getGameType().getPlayersPerTeam() == 1 ? "Игроков" : "Команд") + " осталось: &a" + game.getTeams().getTeamsLeft(),
            "&fРежим: &a" + SkyWars.getGameType().getName(),
            "",
            "     &fwww.villenium.net"
    );

    private static final Function<GamePlayer, List<String>> gameWaitingScoreboardLines = player -> Lists.newArrayList(
            "",
            "&f" + (SkyWars.getGameType().getPlayersPerTeam() == 1 ? "Игроков" : "Команд") + " осталось: &a" + game.getTeams().getTeamsLeft(),
            "&fКарта: &a" + SkyWars.getGameType().getName(),
            "",
            "     &fwww.villenium.net"
    );*/

    public static void setupLobbyScoreboard(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.updateTitle(player, "&6&lSkyWars");
        util.send(player, lobbyScoreboardLines.apply(gamePlayer));
    }

    /*public static void setupGameScoreboard(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.removeAll(gamePlayer.getHandle());
        util.updateTitle(player, "&6&lSkyWars");
        util.send(player, gameScoreboardLines.apply(gamePlayer));
    }*/

    public static void updateSilver(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.send(player, 3, "&fСеребро: &a" + gamePlayer.getCoins());
    }

    public static void updateSouls(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.send(player, 4, "&fДуши: &b" + gamePlayer.getSouls() + "&7/&b???");
    }

}