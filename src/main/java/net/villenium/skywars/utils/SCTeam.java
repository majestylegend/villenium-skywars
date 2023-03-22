package net.villenium.skywars.utils;

import net.villenium.game.api.util.ChatUtil;
import net.villenium.os.packetwrapper.WrapperPlayServerScoreboardTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SCTeam {
    private final String name;
    private final String prefix;
    private final String suffix;
    private List<String> players;

    public SCTeam(final String name, final String prefix, final String suffix, final List<String> players) {
        this.name = name;
        this.prefix = ChatUtil.colorize(prefix);
        this.suffix = ChatUtil.colorize(suffix);
        this.players = players;
    }

    public void create(final Player... players) {
        final WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setMode(0);
        wrapper.setName(this.getName());
        wrapper.setPrefix(this.getPrefix());
        wrapper.setSuffix(this.getSuffix());
        wrapper.setPlayers((List) this.getPlayers());
        for (final Player p : players) {
            wrapper.sendPacket(p);
        }
    }

    public void create(final Collection<Player> players) {
        this.create((Player[]) players.toArray(new Player[players.size()]));
    }

    public void delete(final Player... players) {
        final WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setMode(1);
        wrapper.setName(this.getName());
        wrapper.setPrefix(this.getPrefix());
        wrapper.setSuffix(this.getSuffix());
        wrapper.setPlayers((List) this.getPlayers());
        for (final Player p : players) {
            wrapper.sendPacket(p);
        }
    }

    public void delete(final Collection<Player> players) {
        this.delete((Player[]) players.toArray(new Player[players.size()]));
    }

    public void addPlayerSilently(final Collection<Player> recepients, final Player... players) {
        final WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setMode(3);
        wrapper.setName(this.getName());
        final List<String> added = new ArrayList<String>();
        for (final Player p : players) {
            added.add(p.getName());
        }
        this.players.addAll(added);
        wrapper.setPlayers((List) added);
        recepients.stream().forEach(wrapper::sendPacket);
    }

    public void addPlayer(final Collection<Player> recepients, final Player... players) {
        this.addPlayerSilently(recepients, players);
        this.create(players);
    }

    public void removePlayerSilently(final Collection<Player> recepients, final Player... players) {
        final WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setMode(4);
        wrapper.setName(this.getName());
        final List<String> removed = new ArrayList<String>();
        for (final Player p : players) {
            removed.add(p.getName());
        }
        if (!this.players.removeAll(removed)) {
            return;
        }
        wrapper.setPlayers((List) removed);
        recepients.stream().forEach(wrapper::sendPacket);
    }

    public void removePlayer(final Collection<Player> recepients, final Player... players) {
        this.removePlayerSilently(recepients, players);
        this.delete(players);
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public List<String> getPlayers() {
        return this.players;
    }

    public void setPlayers(final List<String> players) {
        this.players = players;
    }
}
