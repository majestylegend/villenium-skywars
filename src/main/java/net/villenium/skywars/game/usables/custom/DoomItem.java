package net.villenium.skywars.game.usables.custom;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.game.GameTeam;
import net.villenium.skywars.game.usables.ActionType;
import net.villenium.skywars.game.usables.UsableItem;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.AlgoUtil;
import net.villenium.skywars.utils.Cooldowns;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DoomItem extends UsableItem {
    private final int radius;
    private final int time;
    private final int cooldown;
    private final boolean oneTarget;

    public DoomItem(int level, int radius, int time, int cooldown, boolean oneTarget) {
        super(new SimpleItemStack(Material.GOLD_SWORD, "&4&lЗаклятье Рока (Ур. " + level + ")", getDescription(radius, time, cooldown, oneTarget)), ActionType.RIGHT);
        this.radius = radius;
        this.time = time;
        this.cooldown = cooldown;
        this.oneTarget = oneTarget;
    }

    private static List<String> getDescription(int radius, int time, int cooldown, boolean oneTarget) {
        return oneTarget ? Lists.newArrayList(new String[]{"&7При использовании запрещает ближайшему", "&7противнику в радиусе &b" + radius + " &7блоков", "&7использовать все предметы и способности,", "&7а также наносить урон в течение &b" + time + " &7секунд.", "", "&7Перезарядка: &b" + cooldown + " &7секунд."}) : Lists.newArrayList(new String[]{"&7При использовании запрещает всем", "&7противникам в радиусе &b" + radius + " &7блоков", "&7использовать все предметы и способности,", "&7а также наносить урон в течение &b" + time + " &7секунд.", "", "&7Перезарядка: &b" + cooldown + " &7секунд."});
    }

    public void onUse(Player player, ActionType actionType) {
        if (((GameShard) GamePlayer.wrap(player).getShard()).getGamePhase() == GamePhase.INGAME) {
            long left = Cooldowns.check(player, "Doom", (long) this.cooldown);
            if (left != 0L) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы сможете использовать это заклинание через &b%d&c.", new Object[]{left}));
            } else {
                Set<Player> targets = new HashSet();
                GameTeam myTeam = GamePlayer.wrap(player).getTeam();
                Location myLocation = player.getLocation();
                if (this.oneTarget) {
                    Player target = (Player) AlgoUtil.getNearbyEntities(myLocation, Player.class, this.radius).stream().filter((p) -> {
                        return p.getGameMode() == GameMode.SURVIVAL && GamePlayer.wrap(p).getTeam() != myTeam;
                    }).min(Comparator.comparingDouble((p) -> {
                        return p.getLocation().distance(myLocation);
                    })).orElse(null);
                    if (target != null) {
                        targets.add(target);
                    }
                } else {
                    AlgoUtil.getNearbyEntities(myLocation, Player.class, this.radius).stream().filter((p) -> {
                        return p.getGameMode() == GameMode.SURVIVAL && GamePlayer.wrap(p).getTeam() != myTeam;
                    }).forEach(targets::add);
                }
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&eВы использовали &4&lЗаклятье Рока&e!"));
                targets.forEach((targetx) -> {
                    GamePlayer.wrap(player).getShard().pb("%s &cне может использовать предметы и атаковать в течение &b%d &cсекунд.", new Object[]{GameApi.getUserManager().get(targetx).getFullDisplayName(), this.time});
                    GamePlayer.wrap(targetx).setDoomedUntil(System.currentTimeMillis() + (long) this.time * 1000L);
                });
            }
        }
    }
}