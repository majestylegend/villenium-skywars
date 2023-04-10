package net.villenium.skywars.shards;

import lombok.Getter;
import lombok.Setter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.GameMap;
import net.villenium.skywars.handler.GameHandler;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.WorldUtil;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@Setter
public class Shard {
    private static final Map<String, Shard> shards = new ConcurrentHashMap();
    private static final Set<Shard> pendingRestoring = new HashSet();
    public static Map<GameType, List<GameMap>> gameMaps = new HashMap<>();
    private final Collection<Player> players = new HashSet();
    private String id;
    private World world;
    private boolean sleeping = true;

    public Shard(String id) {
        this.id = id;
        shards.put(id, this);
    }

    public static void init() {
        List<GameMap> solo = new ArrayList<>();
        List<GameMap> team = new ArrayList<>();
        if (SkyWars.getInstance().getConfig().isSet("gameMaps.solo")) {
            if (SkyWars.getInstance().getConfig().getConfigurationSection("gameMaps.solo").getKeys(false).size() > 0) {
                for (String name : SkyWars.getInstance().getConfig().getConfigurationSection("gameMaps.solo").getKeys(false)) {
                    ConfigurationSection section = SkyWars.getInstance().getConfig().getConfigurationSection("gameMaps.solo." + name);
                    String visibleName = section.getString("visibleName");
                    List<String> spawnLocations = section.getStringList("spawnLocations");
                    List<String> deathMatch = section.getStringList("deathmatchLocations");
                    String waitingLocation = section.getString("waitingLocation");
                    int maxPlayers = section.getInt("maxPlayers");
                    solo.add(new GameMap(name, visibleName, spawnLocations, deathMatch, waitingLocation, maxPlayers));
                }
            }
            gameMaps.put(GameType.SOLO_CLASSIC, solo);
            gameMaps.put(GameType.SOLO_INSANE, solo);
        }
        if (SkyWars.getInstance().getConfig().isSet("gameMaps.team")) {
            if (SkyWars.getInstance().getConfig().getConfigurationSection("gameMaps.team").getKeys(false).size() > 0) {
                for (String name : SkyWars.getInstance().getConfig().getConfigurationSection("gameMaps.team").getKeys(false)) {
                    ConfigurationSection section = SkyWars.getInstance().getConfig().getConfigurationSection("gameMaps.team." + name);
                    String visibleName = section.getString("visibleName");
                    List<String> spawnLocations = section.getStringList("spawnLocations");
                    List<String> deathMatch = section.getStringList("deathmatchLocations");
                    String waitingLocation = section.getString("waitingLocation");
                    int maxPlayers = section.getInt("maxPlayers");
                    team.add(new GameMap(name, visibleName, spawnLocations, deathMatch, waitingLocation, maxPlayers));
                }
            }
            gameMaps.put(GameType.TEAM_CLASSIC, team);
            gameMaps.put(GameType.TEAM_INSANE, solo);
        }
    }

    public static Shard getShard(String id) {
        return shards.get(id);
    }

    private static void toLobby(Player p, String msg) {
        p.sendMessage(ChatUtil.colorize(msg));
        Task.schedule(() -> GamePlayer.wrap(p).moveToShard(getRandomLobby()), 5L);
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

    public static Collection<Shard> getAllStartedGames() {
        Collection<Shard> shards = Shard.shards.values();
        return shards.stream()
                .filter(shard -> shard instanceof GameShard && ((GameShard)shard).getGamePhase() == GamePhase.INGAME)
                .collect(Collectors.toList());
    }

    public static void processJoinEvent(Player p) {
        if (GamePlayer.wrap(p).getShard() == null) return;
        String shardId = GamePlayer.wrap(p).getShard().getId();
        if (shardId == null) {
            toLobby(p, "&cМы не нашли игровой шард, к которому вы подключаетесь.");
        } else {
            Shard shard = shards.get(shardId);
            if (shard == null) {
                toLobby(p, "&cИгровой шард, к которому вы подключаетесь, не существует! Свяжитесь с администрацией проекта.");
            } else {
                shard.addPlayer(p);
                if (shard instanceof GameShard) {
                    if (((GameShard) shard).getGamePhase() == GamePhase.WAITING) {
                        shard.broadcastRaw(ChatUtil.colorize("&7[&b" + shard.getPlayers().size() + "&7/&b" + ((GameShard) shard).getPlayersMaximumAllowed() + "&7] " + GameApi.getUserManager().get(p).getFullDisplayName() + " &eподключился"));
                    }
                }
            }
        }
    }

    public static int getOnline(GameType gameType) {
        Collection<Shard> shards = Shard.shards.values().stream().filter((shard -> shard instanceof GameShard && ((GameShard) shard).getGameType() == gameType)).collect(Collectors.toList());
        int online = shards.stream().mapToInt(shard -> shard.getPlayers().size()).sum();
        return online;
    }

    public static void processQuitEvent(Player p) {
        if (GamePlayer.wrap(p).getShard() == null) return;
        Shard shard = GamePlayer.wrap(p).getShard();
        if (shard != null) {
            shard.players.remove(p);
            if (shard instanceof GameShard) {
                if (((GameShard) shard).getGamePhase() == GamePhase.WAITING) {
                    shard.broadcastRaw(ChatUtil.colorize("&7[&b" + shard.getPlayers().size() + "&7/&b" + ((GameShard) shard).getPlayersMaximumAllowed() + "&7] " + GameApi.getUserManager().get(p).getFullDisplayName() + " &eвышел"));
                }
                GameHandler.onQuit(p);
                if (GamePlayer.wrap(p).getTeam() != null) {
                    GamePlayer.wrap(p).getTeam().remove(p);
                }
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

    public static Shard findGame(GameType gameType, int players) {
        List<Shard> finded = shards.values().stream().filter((shard -> shard instanceof GameShard && ((GameShard) shard).getGameType() == gameType && ((GameShard) shard).getGamePhase() == GamePhase.WAITING && (((GameShard) shard).getPlayersMaximumAllowed() - shard.getPlayers().size()) >= players)).collect(Collectors.toList());
        if (!finded.isEmpty()) {
            return finded.get(0);
        } else if (gameMaps.get(gameType) != null) {
            GameMap gameMap = gameMaps.get(gameType).get(new Random().nextInt(gameMaps.get(gameType).size()));
            return new GameShard(UUID.randomUUID().toString(), gameType, gameMap, gameMap.getMaxPlayers(), gameType.getPlayersPerTeam());
        } else {
            return null;
        }
    }

    public final void setWorld(World world) {
        this.world = world;
    }

    public final void addPlayer(Player p) {
        this.players.add(p);
    }

    protected void invalidate() {
        shards.remove(this.getId());
        WorldUtil.unloadWorld(world.getName());
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

    public Collection<Player> getPlayers() {
        return this.players.stream().filter((player -> player != null && player.isOnline())).collect(Collectors.toList());
    }
}