package net.villenium.skywars.shards;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.Title;
import net.villenium.game.api.party.Party;
import net.villenium.game.api.user.User;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.Timer;
import net.villenium.skywars.game.*;
import net.villenium.skywars.game.generators.ChestGenerator;
import net.villenium.skywars.game.generators.ClassicGenerator;
import net.villenium.skywars.game.generators.InsaneGenerator;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.player.VScoreboard;
import net.villenium.skywars.utils.BarUtil;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.WorldUtil;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import net.villenium.skywars.utils.simple.SimplePotionEffect;
import net.villenium.skywars.utils.structures.Pair;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GameShard extends Shard {
    private final GameTeams teams;
    private final Set<Player> ggs = new HashSet();
    public int teamsCount;
    private GamePhase gamePhase;
    private GameType gameType;
    private int playersMaximumAllowed;
    private int playersPerTeam;
    private ChestGenerator chestGenerator;
    private int playersOnStart;
    private List<GamePlayer> gamePlayers;
    private GameMap map;
    private Timer timer;
    private long gameStarted;
    private boolean gameEnded;
    private boolean sorted;
    private boolean deathmatchLock;
    private Map<Player, GameClass> selected = new HashMap<>();

    public GameShard(String id, GameType gameType, GameMap gameMap, int maxPlayers, int playersPerTeam) {
        super(id);
        this.gamePhase = GamePhase.WAITING;
        this.playersMaximumAllowed = maxPlayers;
        this.gameType = gameType;
        this.map = gameMap;
        this.playersPerTeam = playersPerTeam;
        this.playersOnStart = 0;
        this.teamsCount = 0;
        this.gamePlayers = new ArrayList<>();
        this.sorted = false;
        this.gameEnded = false;
        this.deathmatchLock = false;
        this.gameStarted = 0;
        this.teams = new GameTeams(this);
        this.timer = new Timer(this);
        this.timer.init();
        new CompassHandler(this).init();
        this.chestGenerator = (gameType.isInsane() ? new InsaneGenerator() : new ClassicGenerator());
        WorldUtil.cloneWorld(map.getName(), this.getId());
        this.setWorld(Bukkit.getWorld(this.getId()));
        this.getWorld().getEntities().forEach((l) -> {
            if (!(l instanceof Player)) l.remove();
        });
        this.getWorld().setGameRuleValue("announceAdvancements", "false");
        this.setSleeping(false);
    }

    private static String getNiceString(String name, Object... args) {
        return getNiceString(String.format(name, args));
    }

    private static String getNiceString(String name) {
        name = ChatUtil.colorize(name);
        String sname = ChatColor.stripColor(name);
        int length = sname.length() >> 1;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 37 - length; ++i) {
            sb.append(" ");
        }

        sb.append(name);
        return sb.toString();
    }

    public void toWaiting(Player p) {
        p.setPlayerListName(GameApi.getUserManager().get(p).getFullDisplayName());
        Location location = map.getWaitingLocation();
        location.setWorld(getWorld());
        GamePlayer.wrap(p).resetPlayer();
        GamePlayer.wrap(p).resetPlayerInventory();
        p.teleport(location);
        p.setFlying(false);
        p.setAllowFlight(false);
        VScoreboard.setupGameWaitingScoreboard(GamePlayer.wrap(p));
        KitSelector.onJoin(p);
    }

    public void startDeathmatch() {
        this.deathmatchLock = true;
            Iterator<Location> spawns = this.getMap().getDeathMatchIterator();
            Iterator teamIterator = this.getTeams().getTeams().iterator();

            while (teamIterator.hasNext()) {
                GameTeam team = (GameTeam) teamIterator.next();
                if (spawns.hasNext()) {
                    Location spawn = spawns.next();
                    spawn.setWorld(getWorld());
                    Location tpTo = spawn.clone();
                    tpTo.setX((double) tpTo.getBlockX() + 0.5D);
                    tpTo.setZ((double) tpTo.getBlockZ() + 0.5D);
                    Iterator var8 = team.getPlayers().iterator();

                    while (var8.hasNext()) {
                        Player p2 = (Player) var8.next();
                        p2.teleport(tpTo);
                        BarUtil.updatableTitle(p2, "&aБой через &2%s", 5);
                    }
                }
            }
        Task.schedule(() -> {
            this.deathmatchLock = false;
            this.getPlayers().forEach((player -> {
                GameApi.getTitleManager().sendTitle(player, Title.TitleType.TITLE, "&aВ бой!");
            }));
        }, 100L);
    }

    public void forcefullyEndTheGame(boolean shuttingDown) {
        if (shuttingDown) {
            this.pb("&4&lИгра отменена в связи с перезагрузкой сервера.");
        } else {
            this.pb("&4&lЭта игра продолжается слишком много времени, посему победитель не определен, и она будет завершена.");
        }

        this.getTeams().clear();
        this.endTheGame();
    }

    public void endTheGame() {
        if (!this.gameEnded) {
            this.gameEnded = true;
            if (this.getTeams().getTeams().isEmpty()) {
                this.switchPhase(GamePhase.RELOADING);
            } else {
                GameTeam winner = this.getTeams().getTeams().iterator().next();
                PriorityQueue<GamePlayer> killers = new PriorityQueue((gp1, gp2) -> ((GamePlayer) gp2).getKills() - ((GamePlayer) gp1).getKills());
                killers.addAll(this.gamePlayers);
                this.b("&a&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                this.b(getNiceString("&lSkyWars"));
                this.b("");
                this.b(getNiceString("&e%s:", winner.getPlayersOnStart().size() == 1 ? "Победитель" : "Победители"));
                this.b(getNiceString(winner.getInfo()));
                this.b("");
                boolean top = false;
                GamePlayer gp;
                if (!killers.isEmpty()) {
                    gp = (GamePlayer) killers.poll();
                    this.b(getNiceString("&bТоп-1 убийца: %s &7- %d", GameApi.getUserManager().get(gp.getName()).getFullDisplayName(), gp.getKills()));
                    top = true;
                }

                if (!killers.isEmpty()) {
                    gp = (GamePlayer) killers.poll();
                    this.b(getNiceString("&cТоп-2 убийца: %s &7- %d", GameApi.getUserManager().get(gp.getName()).getFullDisplayName(), gp.getKills()));
                    top = true;
                }

                if (!killers.isEmpty()) {
                    gp = (GamePlayer) killers.poll();
                    this.b(getNiceString("&6Топ-3 убийца: %s &7- %d", GameApi.getUserManager().get(gp.getName()).getFullDisplayName(), gp.getKills()));
                    top = true;
                }

                if (top) {
                    this.b("");
                }

                this.b("&a&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                Iterator var8 = winner.getPlayersOnStart().iterator();

                while (var8.hasNext()) {
                    Player p = (Player) var8.next();
                    gp = GamePlayer.wrap(p);

                    gp.addSoloWin(50);
                }
                this.getPlayers().forEach((p) -> {
                    ChatUtil.sendClickableMessage(p, "&a&lНачать новую игру?", Arrays.asList("&f*сюда можно нажать*"), "/replay " + this.getGameType().toString());
                });


                this.switchPhase(GamePhase.ENDING);
            }
        }
    }

    public void gg(Player p) {
        if (this.gamePhase == GamePhase.ENDING && !this.ggs.contains(p)) {
            this.ggs.add(p);
            GamePlayer.wrap(p).changeCoins(10);
        }

    }


    public void switchPhase(GamePhase new_phase) {
        if (this.gamePhase != new_phase) {
            this.gamePhase = new_phase;
            switch (new_phase) {
                case PREGAME:
                    this.sortPlayersIntoTeams();
                    this.pb("&aИгра начнется через %d секунд!", new Object[]{10});
                    this.getPlayers().forEach((px) -> {
                        GameApi.getTitleManager().sendTitle(px, Title.TitleType.TITLE, "&6&lSkyWars");
                        GameApi.getTitleManager().sendTitle(px, Title.TitleType.SUBTITLE, "&eИгра скоро начнется!");
                    });
                    this.timer.setTime(10);
                    BarUtil.updatableBar(this.getTimer().getBar(), "&eОжидание завершится через &a%s", 10, this);
                    break;
                case INGAME:
                    this.playersOnStart = this.getPlayers().size();
                    this.gamePlayers = this.getPlayers().stream().map(GamePlayer::wrap).collect(Collectors.toList());
                    Set<Location> toRemoveCage = new HashSet();
                    ItemStack compass = new SimpleItemStack(Material.COMPASS, "&6&lСканирую..");
                    Iterator var6 = this.getPlayers().iterator();

                    while (var6.hasNext()) {
                        Player p = (Player) var6.next();
                        GamePlayer gp = GamePlayer.wrap(p);
                        gp.resetPlayer();
                        gp.resetPlayerInventory();
                        p.getInventory().addItem(new ItemStack[]{compass});
                        GameClass gc = gp.getSelectedClass();
                        int level = 1;
                        if (gc == null) {
                            Pair<GameClass, Integer> selectedClassInfo = gp.addRandomClassForGamePurposes();
                            gc = selectedClassInfo.getFirst();
                            level = selectedClassInfo.getSecond();
                            p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВыбранный случайным образом класс: &e%s &e%d-го уровня&a.", new Object[]{gc.getVisibleName(), level}));
                        } else if (!gc.getName().equals("Builder")) {
                            level = gp.getClassLevel(gc);
                        }

                        if (gc != null) {
                            this.selected.put(p, gc);
                            gc.setup(p, level);
                        }

                        GameTeam team = gp.getTeam();
                        if (team == null) {
                            gp.moveToShard(Shard.getRandomLobby());
                        } else {
                            gp.setPlayed(true);

                            team.getSpawns().forEach((spawn) -> {
                                spawn.setWorld(this.getWorld());
                                toRemoveCage.add(spawn);
                            });
                            int perkModifier = gp.getPerkModifier("Speed_Boost");
                            p.addPotionEffect(new SimplePotionEffect(PotionEffectType.SPEED, 1, perkModifier));
                            perkModifier = gp.getPerkModifier("Resistance_Boost");
                            p.addPotionEffect(new SimplePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1, perkModifier));
                            GameApi.getTitleManager().sendTitle(p, Title.TitleType.TITLE, "&a&lИгра началась!");
                            GameApi.getTitleManager().sendTitle(p, Title.TitleType.SUBTITLE, "&eСкорее лутайся и убей их всех!");
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_CHIME, 1.0F, 1.0F);
                        }
                    }

                    toRemoveCage.forEach(Cage::clear);
                    this.gameStarted = System.currentTimeMillis();
                    break;
                case ENDING:
                    this.timer.setTime(15);
                    BarUtil.updatableBar(this.getTimer().getBar(), "&eПерезагрузка сервера через &a%s", 15, this);
                    break;
                case RELOADING:
                    this.getPlayers().forEach((px) -> {
                        this.getTimer().getBar().removePlayer(px);
                        GamePlayer.wrap(px).moveToShard(Shard.getRandomLobby());
                    });
                    this.timer.setTime(0);
                    Task.schedule(() -> {
                        this.setSleeping(true);
                        invalidate();
                    }, 40L);
            }

        }
    }

    private void sortPlayersIntoTeams() {
        int perTeam = getGameType().getPlayersPerTeam();
        List<Player> solo = new ArrayList<>();
        List<Player> players = new ArrayList<>(getPlayers());
        Collections.shuffle(players);
            for (Player p : players) {
                GamePlayer gp = GamePlayer.wrap(p);
                if (gp.getTeam() != null)
                    continue;
                List<Player> team = new ArrayList<>();
                team.add(p);
                if (perTeam == 1) {
                    new GameTeam(team, this);
                    continue;
                }
                Party party = GameApi.getUserManager().get(p).getParty();
                boolean created = false;
                if (party != null)
                    for (User player : party.getMembers()) {
                        Player p2 = Bukkit.getPlayerExact(player.getName());
                        if (p2 == null || p == p2 || GamePlayer.wrap(p2).getTeam() != null)
                            continue;
                        team.add(p2);
                        if (team.size() == perTeam) {
                            new GameTeam(team, this);
                            created = true;
                            break;
                        }
                    }
                if (created)
                    continue;
                for (Player p2 : players) {
                    GamePlayer gp2 = GamePlayer.wrap(p2);
                    if (p == p2 || gp2.getTeam() != null || GameApi.getUserManager().get(p2).getParty() != null)
                        continue;
                    team.add(p2);
                    if (team.size() == perTeam) {
                        new GameTeam(team, this);
                        created = true;
                        break;
                    }
                }
                if (created)
                    continue;
                solo.add(p);
            }
            List<Player> current = new ArrayList<>();
            for (Player p : solo) {
                current.add(p);
                if (current.size() == perTeam) {
                    new GameTeam(current, this);
                    current = new ArrayList<>();
                }
            }
            if (!current.isEmpty())
                new GameTeam(current, this);
            setupTeams();
    }

    private void setupTeams() {
        Iterator<Location> spawns = this.getMap().getSpawnsIterator();
        this.getPlayers().forEach((p) -> {
            if (!spawns.hasNext()) {
                p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cНа вас не хватило места :("));
                Task.schedule(() -> {
                    GamePlayer.wrap(p).moveToShard(Shard.getRandomLobby());
                }, 10L);
            } else {
                Location spawn = spawns.next();
                spawn.setWorld(getWorld());
                if (!spawn.isChunkLoaded()) spawn.getWorld().loadChunk(spawn.getChunk());
                Location tpTo = spawn.clone();
                tpTo.setX((double) tpTo.getBlockX() + 0.5D);
                tpTo.setY((double) tpTo.getBlockY() + 1.0D);
                tpTo.setZ((double) tpTo.getBlockZ() + 0.5D);
                GamePlayer gp = GamePlayer.wrap(p);
                Cage cage = null;
                if (cage == null) {
                    cage = CageManager.getCages().get(gp.getSelectedCage());
                }
                if (cage == null) {
                    cage = CageManager.getDefaultCage();
                }

                cage.build(spawn);
                gp.getTeam().setSpawn(spawn);
                p.teleport(tpTo);
                p.setFlying(false);
                p.setAllowFlight(false);
                p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы играете за остров &e#" + gp.getTeam().getId()));
                List<Player> teammates = gp.getTeam().getPlayers().stream().filter((player -> !player.getName().equals(p.getName()))).collect(Collectors.toList());
                if (teammates.size() == 1) {
                    p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&eВаш союзник: " + GameApi.getUserManager().get(teammates.get(0)).getFullDisplayName()));
                } else if (teammates.size() > 1) {
                    List<String> mates = new ArrayList<>();
                    teammates.forEach((player -> {
                        mates.add(GameApi.getUserManager().get(player).getFullDisplayName());
                    }));
                    p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&eВаши союзники: " + String.join("&7, ", mates)));
                }
                p.setPlayerListName("§e#" + gp.getTeam().getId() + " " + p.getPlayerListName());
                VScoreboard.setupGameScoreboard(gp);
            }
        });
    }
}