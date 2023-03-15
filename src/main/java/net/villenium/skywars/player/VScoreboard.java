package net.villenium.skywars.player;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.ScoreBoardUtil;
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

    public static void setupLobbyScoreboard(GamePlayer gamePlayer) {
        Player player = gamePlayer.getHandle();
        util.updateTitle(player, "&6&lSkyWars");
        util.send(player, lobbyScoreboardLines.apply(gamePlayer));
    }

}