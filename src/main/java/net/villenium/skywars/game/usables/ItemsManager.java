package net.villenium.skywars.game.usables;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemsManager implements Listener {
    private static final Map<String, UsableItem> usableItems = new HashMap<>();

    public static Map<String, UsableItem> getUsableItems() {
        return usableItems;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractLowest(PlayerInteractEvent e) {
        e.setCancelled(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack hand = e.getItem();
        if (hand == null)
            return;
        Action a = e.getAction();
        boolean left = (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK);
        boolean right = (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK);
        boolean any = (left || right);
        if (!any)
            return;
        String name = hand.getItemMeta().getDisplayName();
        UsableItem ui = usableItems.get(name);
        if (ui == null)
            return;
        if (ui.getActionType() == ActionType.BOTH || (ui
                .getActionType() == ActionType.LEFT && left) || (ui
                .getActionType() == ActionType.RIGHT && right)) {
            e.setCancelled(true);
            ActionType type = left ? ActionType.LEFT : ActionType.RIGHT;
            ui.onUse(e.getPlayer(), type);
        }
    }
}
