package net.villenium.skywars.lobby;

import com.google.common.collect.Lists;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.menu.Menu;
import net.villenium.game.api.menu.MenuButton;
import net.villenium.game.api.menu.MenuEmptyButton;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.*;
import net.villenium.skywars.game.usables.ActionType;
import net.villenium.skywars.game.usables.UsableItem;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.utils.UtilItem;
import net.villenium.skywars.utils.simple.SimpleItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class LobbyEngine implements Listener {
    private static final MenuButton back;
    public static ItemStack item;

    static {
        back = new MenuButton(Material.ARROW, "Назад", Arrays.asList("&7Нажми, чтобы вернуться в", "&7главное меню.")) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                LobbyEngine.openMainInventory(player);
            }
        };
    }

    private LobbyEngine() {
    }

    public static void init() {
        item = new SimpleItemStack(Material.EMERALD, "&f&lМеню &b&lSkyWars");
        new UsableItem(item, ActionType.RIGHT) {
            public void onUse(Player p, ActionType actionType) {
                LobbyEngine.openMainInventory(p);
            }
        };
        Bukkit.getPluginManager().registerEvents(new LobbyEngine(), SkyWars.getInstance());
        Bukkit.getPluginManager().registerEvents(new SoulWell(), SkyWars.getInstance());
    }

    private static void openMainInventory(Player p) {
        final Menu main = GameApi.getMenuUtil().create("Меню SkyWars", 5);
        main.addItem(new MenuButton(Material.STAINED_GLASS, 9, "Клетки", Arrays.asList("&7Нажми, чтобы просмотреть имеющиеся", "&7у тебя стили клеток и выбрать", "&7наиболее понравившийся из них.")) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                getCagesInventory(player).open(player);
            }
        }, 2, 5);
        main.addItem(new MenuButton(Material.GOLD_AXE, "Перки", Arrays.asList("&7Нажми, чтобы просмотреть активные", "&7игровые перки.")) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                getPerksInventory(player).open(player);
            }
        }, 2, 7);
        main.addItem(new MenuButton(Material.DIAMOND_SWORD, "Магазин игровых классов", Arrays.asList("&7Нажми, чтобы просмотреть имеющиеся", "&7у тебя игровые классы или купить новые.")) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
                getClassesInventory(player).open(player);
            }
        }, 2, 3);
        main.open(p);
    }

    private static Menu getCagesInventory(Player p) {
        Menu inv = GameApi.getMenuUtil().create("Стили клетки", 6);
        GamePlayer gp = GamePlayer.wrap(p);
        int line = 2;
        int slot = 2;
        Iterator var5 = CageManager.getCages().values().iterator();

        while (var5.hasNext()) {
            Cage cage = (Cage) var5.next();
            ItemStack is = cage.getIcon().clone();
            ItemMeta im = is.getItemMeta();
            List<String> lore = im.getLore();
            if (lore == null) {
                lore = new ArrayList();
            }

            lore.add("");
            lore.add(ChatUtil.colorize("&7Редкость: %s", new Object[]{cage.getRarity().getVisibleName()}));
            if (!gp.hasCage(cage)) {
                lore.add(ChatUtil.colorize("&bМожно найти в колодце душ!"));
            } else if (CageManager.getCages().get(gp.getSelectedCage()) != cage) {
                lore.add(ChatUtil.colorize("&aУ вас имеется этот стиль."));
            } else {
                lore.add(ChatUtil.colorize("&2У вас выбран этот стиль."));
            }

            im.setLore(lore);
            is.setItemMeta(im);
            inv.addItem(new LobbyEngine.CageItem(is, cage, gp.hasCage(cage)), line, slot);
            ++slot;
            if (slot == 9) {
                ++line;
                slot = 2;
            }
        }

        inv.addItem(new MenuButton(Material.IRON_INGOT, "&7Всего серебра: &6" + gp.getCoins(), new ArrayList()) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {

            }
        }, 6, 5);
        inv.addItem(back, 6, 4);
        return inv;
    }

    private static Menu getPerksInventory(Player p) {
        Menu inv = GameApi.getMenuUtil().create("Игровые перки", 6);
        GamePlayer gp = GamePlayer.wrap(p);
        int line = 2;
        int slot = 2;
        Iterator var5 = GamePerkManager.getPerks().iterator();

        while (var5.hasNext()) {
            GamePerk perk = (GamePerk) var5.next();
            int level = gp.getPerkLevel(perk);
            ItemStack icon = perk.getIcon(level);
            ItemMeta im = icon.getItemMeta();
            List<String> lore = im.getLore();
            if (lore == null) {
                lore = new ArrayList();
            }

            lore.add("");
            lore.add(ChatUtil.colorize("&7Редкость: %s", new Object[]{perk.getRarity().getVisibleName()}));
            if (level == perk.getLevels()) {
                lore.add(ChatUtil.colorize("&2Перк улучшен до последнего уровня."));
            } else if (level == 0) {
                lore.add(ChatUtil.colorize("&bМожно найти в колодце душ!"));
            } else {
                lore.add(ChatUtil.colorize("&bМожно улучшить, найдя еще раз в колодце душ!"));
            }

            im.setLore(lore);
            icon.setItemMeta(im);
            inv.addItem(new LobbyEngine.PerkItem(icon, perk, level), line, slot);
            ++slot;
            if (slot == 9) {
                ++line;
                slot = 2;
            }
        }

        inv.addItem(new MenuButton(Material.IRON_INGOT, "&7Всего серебра: &6" + gp.getCoins(), new ArrayList()) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {
            }
        }, 6, 5);
        inv.addItem(back, 6, 4);
        return inv;
    }

    private static Menu getClassesInventory(Player p) {
        Menu inv = GameApi.getMenuUtil().create("Игровые классы", 6);
        GamePlayer gp = GamePlayer.wrap(p);
        final int[] COST = new int[]{5000, 10000, 25000, 50000, 100000};
        int line = 2;
        int slot = 2;
        Iterator var5 = GameClassManager.getClasses().iterator();

        while (var5.hasNext()) {
            GameClass gc = (GameClass) var5.next();
            Material material = gc.getIcon(GameType.SOLO_CLASSIC, 1).getType();
            List<String> lore = new ArrayList();
            lore.add(ChatUtil.colorize("&7Нажми левой кнопкой мыши для"));
            lore.add(ChatUtil.colorize("&7просмотра подробной информации"));
            lore.add(ChatUtil.colorize("&7об этом классе."));
            lore.add("");
            String name;
            if (gp.getClassLevel(gc) != gc.getLevels() && !gc.getName().equals("Builder")) {
                name = gc.getVisibleName() + ": " + GameClass.getLevel(gp.getClassLevel(gc)) + " -> " + GameClass.getLevel(gp.getClassLevel(gc) + 1);
                if (gp.getCoins() >= COST[gp.getClassLevel(gc)]) {
                    lore.add(ChatUtil.colorize("&aНажми правой кнопкой мыши, чтобы"));
                    lore.add(ChatUtil.colorize("&a%s класс за &e%d &aсеребра.", new Object[]{gp.getClassLevel(gc) == 0 ? "купить" : "улучшить", COST[gp.getClassLevel(gc)]}));
                } else {
                    lore.add(ChatUtil.colorize("&cСтоимость %s: &e%d &cсеребра.", new Object[]{gp.getClassLevel(gc) == 0 ? "класса" : "улучшения класса", COST[gp.getClassLevel(gc)]}));
                }
            } else {
                int level = gc.getName().equals("Builder") ? 1 : gp.getClassLevel(gc);
                name = gc.getVisibleName() + " " + GameClass.getLevel(level);
                lore.add(ChatUtil.colorize("&2У вас имеется этот класс, и он"));
                lore.add(ChatUtil.colorize("&2улучшен до максимального уровня."));
            }

            ItemStack icon = new SimpleItemStack(material, name, lore);
            if (gp.getClassLevel(gc) == gc.getLevels()) {
                icon = UtilItem.glow((ItemStack) icon);
            }
            inv.addItem(new LobbyEngine.ClassInfoItem(icon, gp, gc), line, slot);
            ++slot;
            if (slot == 9) {
                ++line;
                slot = 2;
            }
        }

        inv.addItem(new MenuButton(Material.IRON_INGOT, "&7Всего серебра: &6" + gp.getCoins(), new ArrayList()) {
            @Override
            public void onClick(Player player, ClickType clickType, int slot) {

            }
        }, 6, 5);
        inv.addItem(back, 6, 4);
        return inv;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getInventory().setItem(4, item);
    }

    private static class CageItem extends MenuButton {
        private final Cage cage;
        private final boolean has;

        public CageItem(ItemStack is, Cage cage, boolean has) {
            super(is);
            this.cage = cage;
            this.has = has;
        }

        @Override
        public void onClick(Player player, ClickType clickType, int slot) {
            if (this.has) {
                GamePlayer gp = GamePlayer.wrap(player);
                if (CageManager.getCages().get(gp.getSelectedCage()) == this.cage) {
                    player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cУ вас уже выбран этот стиль."));
                } else {
                    gp.selectCage(this.cage);
                    player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aНовый стиль для клетки успешно выбран."));
                    player.closeInventory();
                }
            } else {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&bМожно найти в колодце душ!"));
            }
        }
    }

    private static class PerkItem extends MenuButton {
        private final GamePerk perk;
        private final int level;

        public PerkItem(ItemStack is, GamePerk perk, int level) {
            super(is);
            this.perk = perk;
            this.level = level;
        }

        @Override
        public void onClick(Player player, ClickType clickType, int slot) {
            if (this.level == 0) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&bМожно найти в колодце душ!"));
            } else if (this.level == this.perk.getLevels()) {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&2Перк улучшен до последнего уровня."));
            } else {
                player.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&bМожно улучшить, найдя еще раз в колодце душ!"));
            }
        }
    }

    private static class ClassInfoItem extends MenuButton {
        static final MenuButton BACK;
        static final List<String> DESCRIPTION;
        static final MenuButton[] GAME_MODES;
        private static final int[] COST = new int[]{5000, 10000, 25000, 50000, 100000};

        static {
            BACK = new MenuButton(Material.BARRIER, "&cНазад", Lists.newArrayList(new String[]{"&7Нажми, чтобы вернуться в магазин классов."})) {
                @Override
                public void onClick(Player player, ClickType clickType, int slot) {
                    getClassesInventory(player).open(player);
                }
            };
            DESCRIPTION = Lists.newArrayList(new String[]{"&7Все иконки этой строки справа", "&7характеризуют выбранный класс", "&7для данного режима."});
            GAME_MODES = new MenuEmptyButton[]{new MenuEmptyButton(Material.IRON_SWORD, "&fРежим: классический соло", DESCRIPTION), new MenuEmptyButton(Material.DIAMOND_SWORD, "&cРежим: безумие соло", DESCRIPTION), new MenuEmptyButton(Material.IRON_HOE, "&fРежим: классический командный", DESCRIPTION), new MenuEmptyButton(Material.DIAMOND_HOE, "&cРежим: командное безумие", DESCRIPTION)};
        }

        private final GameClass cls;
        private final int level;

        public ClassInfoItem(ItemStack is, GamePlayer gp, GameClass gc) {
            super(is);
            this.level = gp.getClassLevel(gc);
            this.cls = gc;
        }

        @Override
        public void onClick(Player p, ClickType type, int slot) {
            if (type == ClickType.RIGHT) {
                if (this.level != this.cls.getLevels() && !this.cls.getName().equals("Builder")) {
                    GamePlayer gp = GamePlayer.wrap(p);
                    if (gp.getCoins() >= COST[this.level]) {
                        gp.changeCoins(-COST[this.level]);
                        gp.addClass(this.cls, this.level + 1);
                        if (this.level == 0) {
                            p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы приобрели новый класс: &e%s&a!", new Object[]{this.cls.getVisibleName()}));
                        } else {
                            p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aВы улучшили класс &e%s &aдо &e%d-го &aуровня!", new Object[]{this.cls.getVisibleName(), this.level + 1}));
                        }

                        p.closeInventory();
                    } else {
                        p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cУ вас недостаточно серебра для покупки."));
                    }
                } else {
                    p.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&2Этот класс у вас имеется, и он улучшен до максимального уровня."));
                }
            } else {
                Menu inv = GameApi.getMenuUtil().create("Класс " + this.cls.getVisibleName(), 6);
                inv.addItem(BACK, 6, 9);
                int levels = this.cls.getLevels();
                GameType[] var6 = GameType.values();
                int var7 = var6.length;

                for (int var8 = 0; var8 < var7; ++var8) {
                    GameType gt = var6[var8];
                    int line = 2 + gt.ordinal();
                    inv.addItem(GAME_MODES[gt.ordinal()], line, 1);
                    int i = 3;

                    for (int lvl = 1; lvl <= levels; ++lvl) {
                        inv.addItem(new MenuEmptyButton(this.cls.getIcon(gt, lvl)), line, i);
                        ++i;
                    }
                }

                inv.open(p);
            }

        }
    }
}