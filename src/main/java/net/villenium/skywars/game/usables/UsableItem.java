package net.villenium.skywars.game.usables;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class UsableItem {
    private final ItemStack icon;

    private final String name;

    private final ActionType actionType;

    public UsableItem(ItemStack icon, ActionType actionType) {
        this.icon = icon;
        this.name = icon.getItemMeta().getDisplayName();
        this.actionType = actionType;
        ItemsManager.getUsableItems().put(this.name, this);
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public String getName() {
        return this.name;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public void takeItemFromHand(Player p) {
        ItemStack is = p.getItemInHand();
        if (is == null)
            return;
        int amount = is.getAmount();
        if (--amount == 0) {
            p.setItemInHand(null);
        } else {
            is.setAmount(amount);
            p.setItemInHand(is);
        }
    }

    public abstract void onUse(Player paramPlayer, ActionType paramActionType);
}
