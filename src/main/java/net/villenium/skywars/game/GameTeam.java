package net.villenium.skywars.game;

import net.villenium.game.api.GameApi;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.SCTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameTeam {
    private final GameShard game;
    private final Set<Player> players = new HashSet();
    private final Set<Player> playersOnStart = new HashSet();
    private SCTeam friends;
    private SCTeam enemies;
    private String info;
    private Location spawn;

    public GameTeam(Collection<Player> players, GameShard game) {
        this.game = game;
        game.getTeams().getTeams().add(this);
        this.players.addAll(players);
        this.playersOnStart.addAll(players);
        StringBuilder sb = new StringBuilder();
        players.forEach((p) -> {
            sb.append(GameApi.getUserManager().get(p).getFullDisplayName()).append(ChatColor.RESET).append(", ");
            GamePlayer.wrap(p).setTeam(this);
        });
        this.info = sb.toString();
        this.info = this.info.substring(0, this.info.length() - 2);
        this.loadTab();
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
            GamePlayer.wrap(p).setTeam((GameTeam) null);
            this.friends.removePlayerSilently(this.players, new Player[]{p});
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
        } else {
            this.enemies.removePlayerSilently(this.players, new Player[]{p});
        }

    }

    public boolean isInTeam(Player p) {
        return this.players.contains(p);
    }

    private void loadTab() {
        List<String> list = (List) this.players.stream().map(OfflinePlayer::getName).collect(Collectors.toList());
        this.friends = new SCTeam("friends", "&a", "", list);
        list = (List) this.game.getPlayers().stream().filter((p) -> {
            return !this.isInTeam(p);
        }).map(OfflinePlayer::getName).collect(Collectors.toList());
        this.enemies = new SCTeam("enemies", "&c", "", list);
        this.friends.create(this.players);
        this.enemies.create(this.players);
    }

    public void showLoseMessage() {
        if (this.playersOnStart.size() > 1) {

        }

    }

    public GameShard getGame() {
        return this.game;
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

    public Location getSpawn() {
        return this.spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}