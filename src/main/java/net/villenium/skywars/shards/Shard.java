package net.villenium.skywars.shards;

import lombok.Getter;
import lombok.Setter;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.handler.GameHandler;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class Shard {
    private static final Map<String, Shard> shards = new ConcurrentHashMap();
    private static final Set<Shard> pendingRestoring = new HashSet();
    private final Collection<Player> players = new HashSet();
    private String id;
    private World world;
    private boolean sleeping = true;

    public Shard(String id) {
        this.id = id;
        shards.put(id, this);
    }

    public static Shard getShard(String id) {
        return (Shard) shards.get(id);
    }

    private static void toLobby(Player p, String msg) {
        p.sendMessage(ChatUtil.colorize(msg));
        Task.schedule(() -> {
            GamePlayer.wrap(p).moveToShard(getRandomLobby());
        }, 5L);
    }

    public static Shard getRandomLobby() {
        Collection<Shard> shards = Shard.shards.values();
        return shards.stream()
                .filter(shard -> shard instanceof LobbyShard)
                .min(Comparator.comparingInt(shard -> shard.getPlayers().size()))
                .orElse(null);
    }

    public static Collection<Shard> getAllLobbies() {
        Collection<Shard> shards = Shard.shards.values();
        return shards.stream()
                .filter(shard -> shard instanceof LobbyShard)
                .collect(Collectors.toList());
    }

    public static void processJoinEvent(Player p) {
        if (GamePlayer.wrap(p).getShard() == null) return;
        String shardId = GamePlayer.wrap(p).getShard().getId();
        if (shardId == null) {
            toLobby(p, "&cМы не нашли игровой шард, к которому вы подключаетесь.");
        } else {
            Shard shard = (Shard) shards.get(shardId);
            if (shard == null) {
                toLobby(p, "&cИгровой шард, к которому вы подключаетесь, не существует! Свяжитесь с администрацией проекта.");
            } else {
                shard.addPlayer(p);
            }
        }

    }

    public static void processQuitEvent(Player p) {
        if (GamePlayer.wrap(p).getShard() == null) return;
        Shard shard = GamePlayer.wrap(p).getShard();
        if (shard != null) {
            shard.players.remove(p);
            if(shard instanceof GameShard) {
                GameHandler.onQuit(p);
                if(GamePlayer.wrap(p).getTeam() != null) {
                    GamePlayer.wrap(p).getTeam().remove(p);
                }
            }
        }
    }

    public static void invalidate(Shard shard, boolean forcefully) {
        if (forcefully) {
            shard.invalidate();
        } else {
            pendingRestoring.add(shard);
            if (shards.values().stream().allMatch(Shard::isSleeping)) {
                pendingRestoring.forEach((gs) -> {
                    gs.invalidate();
                });
                pendingRestoring.clear();
            }
        }
    }

    public static void invalidateAll() {
        shards.values().forEach((shard -> {
            shard.invalidate();
        }));
    }

    public static void broadcastRaw(Player p, String msg) {
        GamePlayer.wrap(p).getShard().broadcastRaw(msg);
    }

    public final void setWorld(World world) {
        this.world = world;
    }

    public final void setWorldByName(String world) {
        if (Bukkit.getWorld(world) != null) {
            this.world = Bukkit.getWorld(world);
        }
    }

    public final void addPlayer(Player p) {
        this.players.add(p);
    }

    protected void invalidate() {
        shards.remove(this.getId());
        WorldUtil.deleteWorld(world);
        this.id = null;
        this.world = null;
        this.players.clear();
    }

    public final void broadcastRaw(String msg) {
        this.players.forEach((p) -> {
            p.sendMessage(msg);
        });
    }

    public final void b(String msg) {
        this.broadcastRaw(ChatUtil.colorize(msg));
    }

    public final void pb(String msg) {
        this.broadcastRaw(ChatUtil.prefixed("&6&lSkyWars", msg));
    }

    public final void pb(String msg, Object[] args) {
        this.broadcastRaw(ChatUtil.prefixed("&6&lSkyWars", String.format(msg, args)));
    }

}