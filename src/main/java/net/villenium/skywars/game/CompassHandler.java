package net.villenium.skywars.game;

import net.villenium.game.api.GameApi;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.Task;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.ConstructorProperties;
import java.util.Optional;

public class CompassHandler {
    private final GameShard game;

    @ConstructorProperties({"game"})
    public CompassHandler(GameShard game) {
        this.game = game;
    }

    private static String getMeters(int meters) {
        int o1 = meters % 10;
        int o2 = meters % 100;
        if (o1 == 1 && o2 != 11) {
            return "метр";
        } else {
            return o1 < 2 || o1 > 4 || o2 >= 10 && o2 <= 20 ? "метров" : "метра";
        }
    }

    public void init() {
        Task.schedule(() -> {
            if (this.game.getGamePhase() == GamePhase.INGAME) {
                this.game.getPlayers().stream().filter((p) -> p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == Material.COMPASS).forEach((p) -> {
                    ItemStack hand = p.getInventory().getItemInMainHand();
                    Location me = p.getLocation();
                    Optional<Player> targetOp = this.game.getPlayers().stream().filter((p2) -> p != p2 && p2.getGameMode() != GameMode.SPECTATOR && p.getWorld() == p2.getWorld() && GamePlayer.wrap(p).getTeam() != GamePlayer.wrap(p2).getTeam()).min((a, b) -> a.getLocation().distance(me) < b.getLocation().distance(me) ? -1 : 1);
                    if (targetOp.isPresent()) {
                        Player target = targetOp.get();
                        p.setCompassTarget(target.getLocation());
                        ItemMeta im = hand.getItemMeta();
                        int meters = (int) me.distance(target.getLocation());
                        im.setDisplayName(ChatUtil.colorize("&6&lЦель: %s &6&l-- &b&l%d &6&l%s", new Object[]{GameApi.getUserManager().get(target).getFullDisplayName(), meters, getMeters(meters)}));
                        hand.setItemMeta(im);
                        p.getInventory().setItemInMainHand(hand);
                    }
                });
            }
        }, 0L, 20L);
    }
}