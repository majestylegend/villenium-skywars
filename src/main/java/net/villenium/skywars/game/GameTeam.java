package net.villenium.skywars.game;

import net.villenium.game.api.GameApi;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.shards.GameShard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class GameTeam {
    private final GameShard game;
    private final Set<Player> players = new HashSet();
    private final Set<Player> playersOnStart = new HashSet();
    private String info;
    private List<Location> spawns = new ArrayList<>();

    private int id;

    public GameTeam(Collection<Player> players, GameShard game) {
        this.game = game;
        game.getTeams().getTeams().add(this);
        this.players.addAll(players);
        this.playersOnStart.addAll(players);
        StringBuilder sb = new StringBuilder();
        this.id = ++game.teamsCount;
        players.forEach((p) -> {
            sb.append(GameApi.getUserManager().get(p).getFullDisplayName()).append(ChatColor.RESET).append(", ");
            GamePlayer.wrap(p).setTeam(this);
        });
        this.info = sb.toString();
        this.info = this.info.substring(0, this.info.length() - 2);
    }

    public void quit(Player p) {
        this.playersOnStart.remove(p);
        this.remove(p);
        GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
        if (game != null) {
            if (game.getGamePhase() == GamePhase.INGAME) {
                game.pb(String.format("%s &e покинул игру", new Object[]{GameApi.getUserManager().get(p).getFullDisplayName()}));
            }
        }
    }

    public void remove(Player p) {
        if (this.players.remove(p)) {
            GamePlayer.wrap(p).setTeam(null);
            if (this.players.isEmpty()) {
                this.game.getTeams().getTeams().remove(this);

                GameShard game = (GameShard) GamePlayer.wrap(p).getShard();
                if (game == null) {
                    return;
                }

                if (game.getGamePhase() == GamePhase.INGAME) {
                    this.showLoseMessage();
                }
            }
        }
        this.getPlayersOnStart().forEach((player -> {
            if(player != null)
                VScoreboard.updateTeamsLeft(GamePlayer.wrap(player));
        }));
    }

    public boolean isInTeam(Player p) {
        return this.players.contains(p);
    }

    public void showLoseMessage() {
        if (this.playersOnStart.size() > 1) {
            this.game.pb("&cКоманда %s &cуничтожена!", new Object[]{this.info});
        }

    }

    public GameShard getGame() {
        return this.game;
    }

    public int getId() {
        return this.id;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public Set<Player> getPlayersOnStart() {
        return this.playersOnStart;
    }

    public String getInfo() {
        return this.info;
    }

    public List<Location> getSpawns() {
        return this.spawns;
    }

    public void setSpawn(Location spawn) {
        this.spawns.add(spawn);
    }
}