package net.villenium.skywars.game;

import net.villenium.skywars.shards.GameShard;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GameTeams {
    private final GameShard game;
    private final Set<GameTeam> teams = new HashSet();

    @ConstructorProperties({"game"})
    public GameTeams(GameShard game) {
        this.game = game;
    }

    public void clear() {
        this.teams.clear();
    }

    public int getTeamsLeft() {
        if (this.teams.size() == 1) {
            return 1;
        } else {
            int total = 0;
            Iterator var2 = this.teams.iterator();

            while (var2.hasNext()) {
                GameTeam gt = (GameTeam) var2.next();
                boolean online = false;
                Iterator var5 = gt.getPlayers().iterator();

                while (var5.hasNext()) {
                    Player p = (Player) var5.next();
                    if (p.isOnline()) {
                        online = true;
                        break;
                    }
                }

                if (online) {
                    ++total;
                }
            }

            return total;
        }
    }

    public GameShard getGame() {
        return this.game;
    }

    public Set<GameTeam> getTeams() {
        return this.teams;
    }
}