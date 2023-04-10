package net.villenium.skywars.game;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.Title;
import net.villenium.game.api.bar.BossBar;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.BarUtil;
import net.villenium.skywars.utils.Task;

import java.beans.ConstructorProperties;

public class Timer {
    private final GameShard game;
    @Getter
    public BossBar bar;
    private int time = 60;

    @ConstructorProperties({"game"})
    public Timer(GameShard game) {
        this.game = game;
    }

    private static String format(int time) {
        return time < 10 ? "0" + time : time + "";
    }

    public void init() {
        bar = GameApi.getBarManager().createDefaultBar("");
        BarUtil.updatableBar(bar, "&eОжидание завершится через &a%s", getTime(), game);
        Task.schedule(() -> {
            bar.addSpigotPlayers(this.game.getPlayers());
            GamePhase current = this.game.getGamePhase();
            int dragonsMinutes;
            int end;
            int minutes;
            switch (current) {
                case WAITING:
                    if (--this.time == 0) {
                        minutes = this.game.getPlayersMaximumAllowed();
                        minutes = minutes * 3 >> 3;
                        if (this.game.getPlayers().size() >= minutes) {
                            this.game.switchPhase(GamePhase.PREGAME);
                        } else {
                            this.time = 60;
                            BarUtil.updatableBar(bar, "&eОжидание завершится через &a%s", getTime(), game);
                            this.game.pb("&cНедостаточно игроков для начала игры!");
                        }
                    } else if (this.game.getPlayers().size() >= this.game.getPlayersMaximumAllowed()) {
                        this.game.switchPhase(GamePhase.PREGAME);
                    }
                    break;
                case PREGAME:
                    if (--this.time == 0) {
                        this.game.switchPhase(GamePhase.INGAME);
                        BarUtil.updatableBar(bar, "&eБессмертие исчезнет через &a%s", 10, game);
                    }
                    break;
                case INGAME:
                    ++this.time;
                    boolean insane = game.getGameType().isInsane();
                    dragonsMinutes = insane ? 12 : 18;
                    end = dragonsMinutes + (insane ? 3 : 4);
                    int chestsRefillSeconds = insane ? 120 : 180;
                    if (this.time == 10) {
                        this.game.pb("&e&lВы более не бессмертны.");
                        BarUtil.updatableBar(bar, "&eДэзматч начнется через &a%s", (dragonsMinutes * 60 - this.time), game);
                    }

                    if (this.time % 60 == 0) {
                        if (this.time == dragonsMinutes * 60) {
                            this.game.startDeathmatch();
                            BarUtil.updatableBar(bar, "&eЗавершение игры через &a%s", end * 60 - this.time, game);
                        } else if (this.time == end * 60) {
                            this.game.forcefullyEndTheGame(false);
                        }
                    }

                    if (this.time % chestsRefillSeconds == 0) {
                        ChestsManager.reset();
                        this.game.pb("&a&lСундуки обновлены!");
                        this.game.getPlayers().forEach((p) -> {
                            GameApi.getTitleManager().sendTitle(p, Title.TitleType.TITLE, "&a&lСундуки обновлены!");
                        });
                    }
                    break;
                case ENDING:
                    if (--this.time == 0) {
                        this.game.switchPhase(GamePhase.RELOADING);
                    }
                    break;
                default:
                    return;
            }

            if (this.time < 0) {
                this.time = 0;
            }

            minutes = this.time / 60;
            dragonsMinutes = this.time % 60;
            end = minutes / 60;
            minutes %= 60;
            String hh = format(end);
            String mm = format(minutes);
            String ss = format(dragonsMinutes);
            GameApi.getScoreboardUtil().updateTitle(game.getPlayers(), ChatUtil.colorize("&7%s:%s:%s &8| %s", new Object[]{hh, mm, ss, current.getVisualName()}));
        }, 0L, 20L);
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}