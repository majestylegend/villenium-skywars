package net.villenium.skywars.shards;

import com.google.common.collect.Iterators;
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
import net.villenium.skywars.utils.AlgoUtil;
import net.villenium.skywars.utils.BarUtil;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.WorldUtil;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import net.villenium.skywars.utils.simple.SimplePotionEffect;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GameShard extends Shard {
    private final GameTeams teams;
    private final Set<Player> ggs = new HashSet();
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

    public GameShard(String id, GameType gameType, GameMap gameMap, int maxPlayers, int playersPerTeam) {
        super(id);
        this.gamePhase = GamePhase.WAITING;
        this.playersMaximumAllowed = maxPlayers;
        this.gameType = gameType;
        this.map = gameMap;
        this.playersPerTeam = playersPerTeam;
        this.playersOnStart = 0;
        this.gamePlayers = new ArrayList<>();
        this.sorted = false;
        this.gameEnded = false;
        this.gameStarted = 0;
        this.teams = new GameTeams(this);
        this.timer = new Timer(this);
        this.timer.init();
        this.chestGenerator = (ChestGenerator) (gameType.isInsane() ? new InsaneGenerator() : new ClassicGenerator());
        Bukkit.broadcastMessage(map.getName());
        WorldUtil.copyWorld(map.getName(), this.getId());
        this.setWorld(Bukkit.getWorld(this.getId()));
        this.getWorld().getEntities().forEach((l) -> {
            if (!(l instanceof Player)) l.remove();
        });
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
        Location location = map.getWaitingLocation();
        location.setWorld(getWorld());
        p.teleport(location);
        VScoreboard.setupGameWaitingScoreboard(GamePlayer.wrap(p));
        KitSelector.onJoin(p);
    }

    public void spawnDragons() {
        Iterator<Location> iterator = this.map.getSpawnsIterator();

        int i;
        for (i = 0; i < AlgoUtil.r(Iterators.size(iterator)); ++i) {
            iterator.next();
        }

        for (i = 0; i < 4; ++i) {
            for (int j = 0; j < AlgoUtil.r(Iterators.size(iterator) >> 2); ++j) {
                iterator.next();
            }

            Location current = (Location) iterator.next();
            EnderDragon dragon = (EnderDragon) current.getWorld().spawn(current, EnderDragon.class);
            dragon.setCustomNameVisible(true);
            dragon.setCustomName(ChatUtil.colorize("&5&lЧерный Дракон"));
            dragon.addPotionEffect(new SimplePotionEffect(PotionEffectType.REGENERATION, 2));
            dragon.addPotionEffect(new SimplePotionEffect(PotionEffectType.ABSORPTION, 2));
        }
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
                PriorityQueue<GamePlayer> killers = new PriorityQueue((gp1, gp2) -> {
                    return ((GamePlayer) gp2).getKills() - ((GamePlayer) gp1).getKills();
                });
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

                    gp.setSoloWins(gp.getSoloWins() + 50);
                }


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
                    Iterator<Location> spawns = this.map.getSpawnsIterator();

                    for (int i = 0; spawns.hasNext(); ++i) {
                        ((Location) spawns.next()).getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
                    }

                    this.sortPlayersIntoTeams();
                    this.pb("&aИгра начнется через %d секунд!", new Object[]{10});
                    this.getPlayers().forEach((px) -> {
                        GameApi.getTitleManager().sendTitle(px, Title.TitleType.TITLE, "&6&lSkyWars");
                        GameApi.getTitleManager().sendTitle(px, Title.TitleType.SUBTITLE, "&eИгра скоро начнется!");
                    });
                    this.timer.setTime(10);
                    BarUtil.updatableTitle(this.getTimer().getBar(), "&eОжидание завершится через &a%s", 10, this);
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
                        /*GameClass gc = gp.getSelectedClass();
                        int level = 1;
                        if (gc == null) {
                            Pair<GameClass, Integer> selectedClassInfo = gp.addRandomClassForGamePurposes();
                            gc = (GameClass)selectedClassInfo.getFirst();
                            level = (Integer)selectedClassInfo.getSecond();
                            p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВыбранный случайным образом класс: &e%s &e%d-го уровня&a.", new Object[]{gc.getVisibleName(), level});
                        } else if (!gc.getName().equals("Builder")) {
                            level = gp.getClassLevel(gc);
                        }

                        if (gc != null) {
                            this.selected.put(p, gc);
                            gc.setup(p, level);
                        }*/

                        GameTeam team = gp.getTeam();
                        if (team == null) {
                            gp.moveToShard(Shard.getRandomLobby());
                        } else {
                            gp.setPlayed(true);
                            Location spawn = team.getSpawn();
                            spawn.setWorld(this.getWorld());
                            toRemoveCage.add(spawn);
                            Location tpTo = spawn.clone();
                            tpTo.setY((tpTo.getBlockY() + 1));
                            tpTo.setX(tpTo.getX() + 0.5D);
                            tpTo.setZ(tpTo.getZ() + 0.5D);
                            p.teleport(tpTo);
                            /*int perkModifier = gp.getPerkModifier("Speed_Boost");
                            p.addPotionEffect(new SimplePotionEffect(PotionEffectType.SPEED, 1, perkModifier));
                            perkModifier = gp.getPerkModifier("Resistance_Boost");
                            p.addPotionEffect(new SimplePotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1, perkModifier));*/
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
                    BarUtil.updatableTitle(this.getTimer().getBar(), "&eПерезагрузка сервера через &a%s", 15, this);
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
        int perTeam = this.getGameType().getPlayersPerTeam();
        Task.schedule(() -> {
            List<Player> solo = new ArrayList();
            List<Player> players = new ArrayList(this.getPlayers());
            Collections.shuffle(players);
            Iterator var4 = players.iterator();

            while (true) {
                while (true) {
                    Player px;
                    GamePlayer gp;
                    do {
                        if (!var4.hasNext()) {
                            List<Player> current = new ArrayList();
                            Iterator var14 = solo.iterator();

                            while (var14.hasNext()) {
                                Player p = (Player) var14.next();
                                current.add(p);
                                if (current.size() == perTeam) {
                                    new GameTeam(current, this);
                                    current = new ArrayList();
                                }
                            }

                            if (!current.isEmpty()) {
                                new GameTeam(current, this);
                            }

                            this.setupTeams();
                            return;
                        }

                        px = (Player) var4.next();
                        gp = GamePlayer.wrap(px);
                    } while (gp.getTeam() != null);

                    List<Player> team = new ArrayList();
                    team.add(px);
                    if (perTeam == 1) {
                        new GameTeam(team, this);
                    } else {
                        Party party = GameApi.getUserManager().get(px).getParty();
                        boolean created = false;
                        Iterator var10;
                        if (party != null) {
                            var10 = party.getMembers().iterator();

                            while (var10.hasNext()) {
                                User player = (User) var10.next();
                                Player p2 = Bukkit.getPlayerExact(player.getName());
                                if (p2 != null && px != p2 && GamePlayer.wrap(p2).getTeam() == null) {
                                    team.add(p2);
                                    if (team.size() == perTeam) {
                                        new GameTeam(team, this);
                                        created = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (!created) {
                            var10 = players.iterator();

                            while (var10.hasNext()) {
                                Player p2x = (Player) var10.next();
                                GamePlayer gp2 = GamePlayer.wrap(p2x);
                                if (px != p2x && gp2.getTeam() == null && GameApi.getUserManager().get(p2x).getParty() == null) {
                                    team.add(p2x);
                                    if (team.size() == perTeam) {
                                        new GameTeam(team, this);
                                        created = true;
                                        break;
                                    }
                                }
                            }

                            if (!created) {
                                solo.add(px);
                            }
                        }
                    }
                }
            }
        });
    }

    private void setupTeams() {
        try {
            Iterator<Location> spawns = this.getMap().getSpawnsIterator();
            int i = 0;
            Iterator var3 = this.getTeams().getTeams().iterator();

            label38:
            while (true) {
                while (true) {
                    if (!var3.hasNext()) {
                        break label38;
                    }

                    GameTeam team = (GameTeam) var3.next();
                    ++i;
                    if (!spawns.hasNext()) {
                        team.getPlayers().forEach((p) -> {
                            p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cНа вас не хватило места :("));
                            Task.schedule(() -> {
                                GamePlayer.wrap(p).moveToShard(Shard.getRandomLobby());
                            }, 10L);
                        });
                    } else {
                        Location spawn = (Location) spawns.next();
                        spawn.setWorld(getWorld());
                        Location tpTo = spawn.clone();
                        tpTo.setX((double) tpTo.getBlockX() + 0.5D);
                        tpTo.setY((double) tpTo.getBlockY() + 1.0D);
                        tpTo.setZ((double) tpTo.getBlockZ() + 0.5D);
                        team.setSpawn(spawn);
                        Cage cage = null;
                        Iterator var8 = team.getPlayers().iterator();

                        while (var8.hasNext()) {
                            Player p2 = (Player) var8.next();
                            GamePlayer gp = GamePlayer.wrap(p2);
                            p2.teleport(tpTo);
                            VScoreboard.setupGameScoreboard(gp);
                            if (cage == null) {
                                cage = gp.getSelectedCage();
                            }
                        }

                        if (cage == null) {
                            cage = CageManager.getDefaultCage();
                        }

                        cage.build(spawn);
                    }
                }
            }
        } catch (Exception var11) {
        }
    }

    protected void invalidate() {
        super.invalidate();
    }
}