package net.villenium.skywars.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.athena.annotation.Id;
import net.villenium.game.api.athena.annotation.IgnoreField;
import net.villenium.game.api.user.User;
import net.villenium.game.api.user.permission.UserPermission;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.game.*;
import net.villenium.skywars.game.usables.custom.WraithItem;
import net.villenium.skywars.handler.GameHandler;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.AlgoUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class GamePlayer {

    @Id
    private final String name;
    @IgnoreField
    private LoadingCache<Player, Long> lastDamagers;
    Map<GameClass, Integer> classes;
    GameClass selectedClass;
    List<Cage> cages;
    Cage selectedCage;
    private int soloKills;
    private int soloWins;
    private int coins;
    private int souls;
    private Map<GamePerk, Integer> perks;
    @IgnoreField
    private long doomedUntil;
    @IgnoreField
    private WraithItem.WraithInfometer wraithInfometer;
    @IgnoreField
    private int kills;
    @IgnoreField
    private Shard shard;
    @IgnoreField
    private GameTeam team;
    @IgnoreField
    private boolean played;
    @IgnoreField
    private double increasedDamage;
    @IgnoreField
    private long leave;
    @IgnoreField
    private int assists;

    public GamePlayer(String name, Map<GameClass, Integer> classes, GameClass selectedClass, List<Cage> cages, Cage selectedCage, int soloKills, int soloWins, int coins, int souls, Map<GamePerk, Integer> perks) {
        this.lastDamagers = CacheBuilder.newBuilder().expireAfterWrite(15L, TimeUnit.SECONDS).weakKeys().build(new CacheLoader<Player, Long>() {
            public Long load(Player k) {
                return System.currentTimeMillis();
            }
        });
        this.classes = classes;
        this.selectedClass = selectedClass;
        this.cages = cages;
        this.selectedCage = selectedCage;
        this.perks = perks;
        this.kills = 0;
        this.assists = 0;
        this.played = false;
        this.doomedUntil = 0L;
        this.name = name;
        this.soloKills = soloKills;
        this.soloWins = soloWins;
        this.coins = coins;
        this.souls = souls;
    }

    public static GamePlayer wrap(String name) {
        if(PlayerManager.cache.containsKey(name)) {
            return PlayerManager.cache.get(name);
        }
        GamePlayer gp = PlayerManager.cache.put(name, SkyWars.getInstance().getPlayerManager().getObjectPool().get(name));
        return gp;
    }

    public static GamePlayer wrap(Player player) {
        return wrap(player.getName());
    }

    public static GamePlayer wrap(User user) {
        return wrap(user.getName());
    }

    public Player getHandle() {
        return Bukkit.getPlayerExact(name);
    }

    public void moveToShard(Shard shard) {
        Shard.processQuitEvent(this.getHandle());
        this.shard = shard;
        Shard.processJoinEvent(this.getHandle());
        this.getHandle().teleport(shard.getWorld().getSpawnLocation());
        Shard game = this.getShard();
        if (game != null) {
            Collection<Player> thatShard = game.getPlayers();
            Bukkit.getOnlinePlayers().stream().filter((p2) -> {
                return !thatShard.contains(p2);
            }).forEach((p2) -> {
                p2.hidePlayer(this.getHandle());
                this.getHandle().hidePlayer(p2);
            });
            thatShard.forEach((p2) -> {
                p2.showPlayer(this.getHandle());
                this.getHandle().showPlayer(p2);
            });
        }
        if (game instanceof GameShard) {
            GameHandler.onJoin(this.getHandle());
        } else {
            ((LobbyShard) shard).setupPlayer(this.getHandle());
        }
    }

    public void resetPlayer() {
        Player p = this.getHandle();
        p.setLevel(0);
        p.setExp(0.0F);
        p.getActivePotionEffects().stream().map(e -> e.getType()).forEach(p::removePotionEffect);
        if (p.getHealth() > 20.0D)
            p.setHealth(20.0D);
        p.setFireTicks(0);
        p.setMaxHealth(20.0D);
        p.setHealth(20.0D);
        p.setFoodLevel(20);
        p.setVelocity(new Vector());
    }

    public void resetPlayerInventory() {
        Player p = this.getHandle();
        p.getInventory().clear();
        p.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[4]);
    }

    public void addCage(Cage cage) {
        this.cages.add(cage);
    }

    public Pair<GameClass, Integer> addRandomClassForGamePurposes() {
        GameClass cls = (GameClass) GameClassManager.getClasses().get(AlgoUtil.r(GameClassManager.getClasses().size()));
        int level = 1 + AlgoUtil.r(cls.getLevels());
        int perkModifier = this.getPerkModifier("CoolRandom");
        if (level < perkModifier) {
            level = Math.min(perkModifier, cls.getLevels());
        }

        this.classes.put(cls, level);
        this.selectedClass = cls;
        return new Pair(cls, level);
    }

    public void addClass(GameClass gc, int level) {
        this.classes.put(gc, level);
    }

    public void addPerk(GamePerk perk, int level) {
        this.perks.put(perk, level);
    }

    public boolean hasPerk(GamePerk gp) {
        return this.perks.containsKey(gp);
    }

    public boolean hasPerk(GamePerk gp, int level) {
        Integer lvl = (Integer) this.perks.get(gp);
        return lvl != null && lvl >= level;
    }

    public boolean hasPerk(String name) {
        return this.hasPerk(GamePerkManager.getPerk(name));
    }

    public int getPerkLevel(GamePerk gp) {
        if (gp == null) {
            return 0;
        } else {
            Integer lvl = (Integer) this.perks.get(gp);
            return lvl == null ? 0 : lvl;
        }
    }

    public int getPerkModifier(GamePerk gp) {
        int level = this.getPerkLevel(gp);
        return level == 0 ? 0 : gp.getModifier(level);
    }

    public int getPerkModifier(String name) {
        return this.getPerkModifier(GamePerkManager.getPerk(name));
    }

    public boolean hasClass(GameClass gc) {
        return this.classes.containsKey(gc);
    }

    public boolean hasClass(GameClass gc, int level) {
        Integer lvl = (Integer) this.classes.get(gc);
        return lvl != null && lvl >= level;
    }

    public int getClassLevel(GameClass gc) {
        Integer lvl = (Integer) this.classes.get(gc);
        return lvl == null ? (gc.getName().equals("Builder") ? 1 : 0) : lvl;
    }

    public void selectClass(GameClass gc) {
        this.selectedClass = gc;
    }

    public boolean hasCage(Cage cage) {
        return this.cages.contains(cage);
    }

    public void selectCage(Cage cage) {
        this.selectedCage = cage;
    }

    public void addLastDamager(Player p) {
        this.lastDamagers.put(p, System.currentTimeMillis());
    }

    public Player getKiller() {
        return this.lastDamagers.asMap().keySet().stream()
                .max((a, b) -> (this.lastDamagers.getUnchecked(a)).compareTo(this.lastDamagers.getUnchecked(b))).orElse(null);
    }

    public Set<Player> getAssistants() {
        Set<Player> assistants = new HashSet(this.lastDamagers.asMap().keySet());
        assistants.remove(this.getKiller());
        return assistants;
    }


    public void addJustCoins(int amount) {
        this.coins += amount;
        Player p = this.getHandle();
        if (p != null) {
            p.sendMessage(ChatUtil.colorize("&6+%d серебра", amount));
        }
    }

    public void addSoloKill(int coins) {
        ++this.kills;
        ++this.soloKills;
        int dc = coins;
        int ds = 1;
        this.coins += dc;
        this.souls = Math.min(this.getSoulsLimit(), this.souls + ds);
        Player p = this.getHandle();
        if (p != null) {
            p.sendMessage(ChatUtil.colorize("&6+%d серебра", dc));
        }

        if (p != null) {
            p.sendMessage(ChatUtil.colorize("&b+%d %s", ds, this.getSoulsName(ds)));
        }
    }

    public void addSoloWin(int coins) {
        ++this.soloWins;
        Player p = this.getHandle();
        int dc = coins;
        this.coins += dc;
        if (p != null) {
            p.sendMessage(ChatUtil.colorize("&6+%d серебра", dc));
        }
    }

    public void changeCoins(int amount) {
        this.coins += amount;
        if (amount > 0 && this.getHandle() != null) {
            this.getHandle().sendMessage(ChatUtil.colorize("&6+%d серебра", amount));
        }

        if (this.getShard() instanceof LobbyShard) {
            VScoreboard.updateSilver(this);
        }
    }

    public int getSoulsLimit() {
        UserPermission up = GameApi.getUserManager().get(this.name).getPermission();
        if (up.isRich()) {
            return 150;
        } else if (up.isVipPlus()) {
            return 130;
        } else {
            return up.isVip() ? 110 : 100;
        }
    }

    public void changeSouls(int amount) {
        int limit = this.getSoulsLimit();
        this.souls = Math.min(limit, this.souls + amount);
        this.souls = Math.max(0, this.souls);
        Player p = this.getHandle();
        if (p != null) {
            if (amount > 0) {
                this.getHandle().sendMessage(ChatUtil.colorize("&b+%d %s", amount, this.getSoulsName(amount)));
            } else {
                this.getHandle().sendMessage(ChatUtil.colorize("&b%d %s", amount, this.getSoulsName(-amount)));
            }

            if (this.getShard() instanceof LobbyShard) {
                VScoreboard.updateSouls(this);
            }
        }
    }

    private String getSoulsName(int amount) {
        int o1 = amount % 10;
        int o2 = amount % 100;
        if (o1 == 1 && o2 != 11) {
            return "душа";
        } else {
            return o1 < 2 || o1 > 4 || o2 >= 10 && o2 <= 20 ? "душ" : "души";
        }
    }

    public void addAssist() {
        ++this.assists;
    }
}