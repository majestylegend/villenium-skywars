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
import java.util.List;

public class BloodMageItem extends UsableItem {
    private final int cooldown;
    private final float health;

    public BloodMageItem(int level, float health, int cooldown) {
        super(new SimpleItemStack(Material.RED_ROSE, "&c&lЗаклятье телепортации (Ур. " + level + ")", getDescription(health, cooldown)), ActionType.RIGHT);
        this.health = health;
        this.cooldown = cooldown;
    }

    private static List<String> getDescription(float health, int cooldown) {
        return Lists.newArrayList(new String[]{"&7Незамедлительно телепортирует вас к", "&7ближайшему противнику, уведомляя его об", "&7этом в чате и забирая у вас &b" + (int) (health * 100.0F) + "%", "&7максимального запаса здоровья.", "", "&7Перезарядка: &b" + cooldown + " &7секунд."});
    }

    public void onUse(Player player, ActionType actionType) {
        if (((GameShard) GamePlayer.wrap(player).getShard()).getGamePhase() == GamePhase.INGAME) {
            long left = Cooldowns.check(player, "BloodMage", (long) this.cooldown);
            if (left != 0L) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы сможете использовать это заклинание через &b%d&c.", new Object[]{left}));
            } else {
                double damage = player.getHealth() * (double) this.health;
                if (player.getHealth() - damage <= 0.5D) {
                    Cooldowns.uncheck(player, "BloodMage");
                    player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cУ вас недостаточно здоровья для использования этой способности.", new Object[0]));
                } else {
                    Location myLocation = player.getLocation();
                    GameTeam myTeam = GamePlayer.wrap(player).getTeam();
                    Player target = (Player) AlgoUtil.getNearbyEntities(myLocation, Player.class, 256).stream().filter((p) -> {
                        return p.getGameMode() == GameMode.SURVIVAL && GamePlayer.wrap(p).getTeam() != myTeam;
                    }).min(Comparator.comparingDouble((p) -> {
                        return p.getLocation().distance(myLocation);
                    })).orElse(null);
                    if (target != null) {
                        player.setHealth(player.getHealth() * (double) (1.0F - this.health));
                        player.damage(0.0D);
                        player.teleport(target.getLocation());
                        player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы телепортировались к %s&a.", new Object[]{GameApi.getUserManager().get(target).getFullDisplayName()}));
                        target.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "%s &cтелепортировался к вам (использовав заклятие телепортации).", new Object[]{GameApi.getUserManager().get(player).getFullDisplayName()}));
                    }

                }
            }
        }
    }
}