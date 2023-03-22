package net.villenium.skywars.game.usables.custom;

import com.google.common.collect.Lists;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.game.usables.ActionType;
import net.villenium.skywars.game.usables.UsableItem;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.Cooldowns;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnderLordItem extends UsableItem {
    private final int cooldown;
    private final float health;
    private final int healthReduction;

    public EnderLordItem(int level, float health, int healthReduction, int cooldown) {
        super(new SimpleItemStack(Material.WRITTEN_BOOK, "&c&lЗаклятье сотворения (Ур. " + level + ")", getDescription(health, healthReduction, cooldown)), ActionType.RIGHT);
        this.health = health;
        this.cooldown = cooldown;
        this.healthReduction = healthReduction;
    }

    private static List<String> getDescription(float health, int healthReduction, int cooldown) {
        return health == 0.0F ? Lists.newArrayList(new String[]{"&7Создает две жемчужины края, снижая", "&7максимальное кол-во вашего здоровья", "&7на &b" + healthReduction + " сердца&7.", "", "&7Перезарядка: &b" + cooldown + " &7секунд."}) : Lists.newArrayList(new String[]{"&7Создает жемчужину края, нанося вам", "&7урон в размере &b" + (int) (health * 100.0F) + "% &7от максимального", "&7запаса здоровья и снижая максимальное", "&7кол-во вашего здоровья на &b" + healthReduction + " сердца&7.", "", "&7Перезарядка: &b" + cooldown + " &7секунд."});
    }

    public void onUse(Player player, ActionType actionType) {
        if (((GameShard) GamePlayer.wrap(player).getShard()).getGamePhase() == GamePhase.INGAME) {
            long left = Cooldowns.check(player, "EnderLord", (long) this.cooldown);
            if (left != 0L) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы сможете использовать это заклинание через &b%d&c.", new Object[]{left}));
            } else {
                double damage = player.getHealth() * (double) this.health;
                if (!(player.getHealth() - damage <= 0.5D) && !(player.getMaxHealth() - (double) this.healthReduction <= 1.0D)) {
                    player.setHealth(player.getHealth() * (double) (1.0F - this.health));
                    double newMaxHealth = player.getMaxHealth() - (double) this.healthReduction;
                    player.setHealth(Math.min(player.getHealth(), newMaxHealth));
                    player.setMaxHealth(newMaxHealth);
                    player.damage(0.0D);
                    player.getInventory().addItem(new ItemStack[]{new SimpleItemStack(Material.ENDER_PEARL, "&5Сотворенная жемчужина", new Object[]{this.health == 0.0F ? 2 : 1})});
                    player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы создали &5%s&a, пожертвовав своим здоровьем!", new Object[]{this.health > 0.0F ? "жемчужину края" : "две жемчужины края"}));
                } else {
                    Cooldowns.uncheck(player, "EnderLord");
                    player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cУ вас недостаточно здоровья для использования этой способности."));
                }
            }
        }
    }
}