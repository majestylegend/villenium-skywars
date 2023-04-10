package net.villenium.skywars.lobby;

import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.enums.Rarity;
import net.villenium.skywars.game.*;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.utils.AlgoUtil;
import net.villenium.skywars.utils.Task;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.*;

public class SoulWell implements Listener {
    private static final Map<Player, SoulWell.SWHandler> soulWells = new HashMap();
    private static final int[] slots = new int[]{4, 13, 22, 31, 40};
    private static Menu main;

    SoulWell() {
        main = GameApi.getMenuUtil().create("Вы уверены?", 3);
        main.addItem(new MenuButton(Material.STAINED_CLAY, 13, "Подтвердить", Arrays.asList("&7Воспользоваться колодцем душ", "&7в поисках случайного перка", "&7или стиля для клетки.", "", "&7Стоимость: &b10 &7душ.")) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                SoulWell.doSoulWell(player);
            }
        }, 2, 3);
        main.addItem(new MenuButton(Material.STAINED_CLAY, 14, "Отклонить", Arrays.asList("&7Ничего не делать и закрыть", "&7это меню.")) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                player.closeInventory();
            }
        }, 2, 7);
    }

    private static void doSoulWell(Player p) {
        if (!soulWells.containsKey(p)) {
            GamePlayer player = GamePlayer.wrap(p);
            player.changeSouls(-10);
            Inventory inv = Bukkit.createInventory(null, 45, "Колодец душ");
            ItemStack black = new SimpleItemStack(Material.STAINED_GLASS, "", new Object[]{Short.valueOf((short) 15)});
            inv.setItem(21, black);
            inv.setItem(23, black);
            List<SoulWell.SoulWellItem> items = new ArrayList();
            Iterator var6 = GamePerkManager.getPerks().iterator();

            SoulWell.SoulWellItem swi;
            Rarity r;
            int copies;
            int i;
            while (var6.hasNext()) {
                GamePerk gp = (GamePerk) var6.next();
                swi = new SoulWell.SoulWellItem(player, null, gp, null);
                r = gp.getRarity();
                copies = r.getCopies();
                if (r != Rarity.COMMON && GameApi.getUserManager().get(p).getPermission().isVipPlus()) {
                    copies <<= 1;
                }

                for (i = 0; i < copies; ++i) {
                    items.add(swi);
                }
            }

            var6 = CageManager.getCages().values().iterator();

            while (var6.hasNext()) {
                Cage cage = (Cage) var6.next();
                swi = new SoulWell.SoulWellItem(player, null, null, cage);
                r = cage.getRarity();
                copies = r.getCopies();
                if (r != Rarity.COMMON && GameApi.getUserManager().get(p).getPermission().isVipPlus()) {
                    copies <<= 1;
                }

                for (i = 0; i < copies; ++i) {
                    items.add(swi);
                }
            }

            Collections.shuffle(items);
            p.closeInventory();
            p.openInventory(inv);
            soulWells.put(p, new SoulWell.SWHandler(p, inv, items));
        }
    }

    @EventHandler(
            ignoreCancelled = false
    )
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
            Player p = e.getPlayer();
            GamePlayer gp = GamePlayer.wrap(p);
            if (gp.getSouls() < 10) {
                p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&c&lДля использования колодца душ необходимо иметь хотя бы &b&l10 &c&lдуш!"));
                if (gp.getName().equals("M4JESTY")) gp.changeSouls(100);
                return;
            }
            main.open(p);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getView().getTopInventory();
        if (inv.getName().equals("Колодец душ")) {
            soulWells.remove(e.getPlayer());
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getView().getTopInventory();
        if (inv.getName().equals("Колодец душ")) {
            e.setCancelled(true);
        }

    }

    private static class SoulWellItem {
        private final GamePlayer gamePlayer;
        private final GameClass gameClass;
        private final GamePerk gamePerk;
        private final Cage cage;

        @ConstructorProperties({"gamePlayer", "gameClass", "gamePerk", "cage"})
        public SoulWellItem(GamePlayer gamePlayer, GameClass gameClass, GamePerk gamePerk, Cage cage) {
            this.gamePlayer = gamePlayer;
            this.gameClass = gameClass;
            this.gamePerk = gamePerk;
            this.cage = cage;
        }

        public ItemStack getIcon() {

            int level;
            if (this.gameClass != null) {
                level = this.gamePlayer.getClassLevel(this.gameClass);
                return this.gameClass.getIcon(GameType.SOLO_CLASSIC, Math.max(1, level));
            } else if (this.gamePerk != null) {
                level = this.gamePlayer.getPerkLevel(this.gamePerk);
                return this.gamePerk.getIcon(Math.max(1, level));
            } else {
                return this.cage.getIcon();
            }
        }

        public GamePlayer getGamePlayer() {
            return this.gamePlayer;
        }

        public GameClass getGameClass() {
            return this.gameClass;
        }

        public GamePerk getGamePerk() {
            return this.gamePerk;
        }

        public Cage getCage() {
            return this.cage;
        }
    }

    private static class SWHandler extends Task {
        private static final int ticks = 50;
        private final Player p;
        private final Inventory inv;
        private final SoulWell.SoulWellItem prize;
        private final List<SoulWell.SoulWellItem> items;
        private int index = 0;
        private int tick = 0;
        private int delay = 0;

        public SWHandler(Player p, Inventory inv, List<SoulWell.SoulWellItem> items) {
            super(SkyWars.getInstance(), "soulwell-" + p.getName(), -1, 0, 100);
            this.p = p;
            this.inv = inv;
            this.prize = items.get(47 % items.size());
            this.items = items;
        }

        public void onTick() {
            if (this.p.isOnline() && SoulWell.soulWells.containsValue(this)) {
                ++this.delay;
                if (this.tick < 20 || this.delay == 1 && this.tick < 30 || this.delay == 2 && this.tick < 45 || this.delay == 4) {
                    this.delay = 0;
                    ++this.tick;

                    for (int i = SoulWell.slots.length - 1; i > 0; --i) {
                        ItemStack previous = this.inv.getItem(SoulWell.slots[i - 1]);
                        if (previous != null) {
                            this.inv.setItem(SoulWell.slots[i], previous);
                        }
                    }

                    this.inv.setItem(SoulWell.slots[0], (this.items.get(this.index++)).getIcon());
                    this.index %= this.items.size();
                    if (this.tick == 50) {
                        this.givePrize(this.p, this.prize);
                        this.cancel();
                    }

                    this.p.playSound(this.p.getLocation(), Sound.BLOCK_NOTE_FLUTE, 1.0F, 1.0F);
                }

            } else {
                this.givePrize(this.p, this.prize);
                this.cancel();
            }
        }

        private void givePrize(Player p, SoulWell.SoulWellItem item) {
            GamePlayer gp = GamePlayer.wrap(p);
            int amount;
            if (item.getGamePerk() != null) {
                GamePerk perk = item.getGamePerk();
                amount = gp.getPerkLevel(perk);
                if (amount == perk.getLevels()) {
                    amount = 25 + AlgoUtil.r(200);
                    gp.changeCoins(amount);
                    if (p.isOnline()) {
                        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы получили перк &e%s&a, но т.к. этот предмет у вас уже есть, вы получили &e%d&a серебра.", new Object[]{perk.getVisibleName(), amount}));
                    }
                } else {
                    gp.addPerk(perk, amount + 1);
                    if (p.isOnline()) {
                        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы получили перк &e%s&a.", new Object[]{perk.getVisibleName()}));
                    }
                }
            } else {
                Cage cage = item.getCage();
                if (gp.hasCage(cage)) {
                    amount = 25 + AlgoUtil.r(200);
                    gp.changeCoins(amount);
                    if (p.isOnline()) {
                        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы получили стиль &e%s&a, но т.к. этот предмет у вас уже есть, вы получили &e%d&a серебра.", new Object[]{cage.getVisualName(), amount}));
                    }
                } else {
                    gp.addCage(cage);
                    if (p.isOnline()) {
                        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы получили стиль &e%s&a.", new Object[]{cage.getVisualName()}));
                    }
                }
            }

        }
    }
}