package net.villenium.skywars.game.usables.custom;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
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

import java.beans.ConstructorProperties;
import java.util.List;

public class WraithItem extends UsableItem {
    private final int time;
    private final int cooldown;
    private final float incoming;
    private final float outcoming;

    public WraithItem(int level, float incoming, float outcoming, int time, int cooldown) {
        super(new SimpleItemStack(Material.GOLD_HOE, "&c&lЗаклятье Ярости (Ур. " + level + ")", getDescription(incoming, outcoming, time, cooldown)), ActionType.RIGHT);
        this.time = time;
        this.cooldown = cooldown;
        this.incoming = incoming;
        this.outcoming = outcoming;
    }

    private static List<String> getDescription(float incoming, float outcoming, int time, int cooldown) {
        return Lists.newArrayList(new String[]{"&7Увеличивает наносимый вами урон на &b" + (int) (outcoming * 100.0F) + "%&7 и", "&7увеличивает получаемый вами урон на &b" + (int) (incoming * 100.0F) + "%&7", "&7на &b" + time + " &7секунды.", "", "&7Перезарядка: &b" + cooldown + " &7секунд."});
    }

    public void onUse(Player player, ActionType actionType) {
        if (((GameShard) GamePlayer.wrap(player).getShard()).getGamePhase() == GamePhase.INGAME) {
            long left = Cooldowns.check(player, "Wraith", (long) this.cooldown);
            if (left != 0L) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы сможете использовать это заклинание через &b%d&c.", new Object[]{left}));
            } else {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&eВы использовали &c&lЗаклятье Ярости&e!"));
                GamePlayer.wrap(player).setWraithInfometer(new WraithItem.WraithInfometer(System.currentTimeMillis() + (long) this.time * 1000L));
                GamePlayer.wrap(player).getShard().pb("&cНаносимый урон у %s &cувеличен на %d%%, получаемый - на %d%% на %d секунд.", new Object[]{GameApi.getUserManager().get(player).getFullDisplayName(), (int) (this.outcoming * 100.0F), (int) (this.incoming * 100.0F), this.time});
            }
        }
    }

    public class WraithInfometer {
        private final long until;

        @ConstructorProperties({"until"})
        public WraithInfometer(long until) {
            this.until = until;
        }

        public float getIncreasedIncomingDamage() {
            return 1.0F + (!this.isOver() ? WraithItem.this.incoming : 0.0F);
        }

        public float getIncreasedOutcomingDamage() {
            return 1.0F + (!this.isOver() ? WraithItem.this.outcoming : 0.0F);
        }

        public boolean isOver() {
            return System.currentTimeMillis() > this.until;
        }
    }
}