package net.villenium.skywars.game;

import net.villenium.skywars.enums.Rarity;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GamePerkManager {
    private static final Map<String, GamePerk> PERKS = new HashMap();

    public static void init() {
        add(new GamePerk("Arrow_Recovery", "Восстановление стрел", "%d%% шанс вернуть стрелу при выстреле.", Rarity.RARE, Material.ARROW, 5, new int[]{0, 5000, 15000, 20000, 25000, 5, 10, 15, 20, 25}));
        add(new GamePerk("Blazing_Arrows", "Горящие стрелы", "%d%% шанс поджечь стрелу при выстреле.", Rarity.RARE, Material.BLAZE_POWDER, 5, new int[]{0, 5000, 15000, 20000, 25000, 1, 2, 3, 4, 5}));
        add(new GamePerk("Bulldozer", "Бульдозер", "Вы получаете %d-секундную силу при убийстве.", Rarity.EPIC, Material.ANVIL, 5, new int[]{0, 5000, 15000, 20000, 25000, 1, 2, 3, 4, 5}));
        add(new GamePerk("Ender_Mastery", "Чародей края", "Урон от эндер-жемчуга уменьшен на %d%%.", Rarity.COMMON, Material.ENDER_PEARL, 5, new int[]{0, 5000, 15000, 20000, 25000, 20, 40, 60, 80, 100}));
        add(new GamePerk("Juggernaut", "Джаггернаут", "Вы получаете %d-секундную регенерацию при убийстве.", Rarity.RARE, Material.DIAMOND_HELMET, 5, new int[]{0, 5000, 15000, 20000, 25000, 2, 4, 6, 8, 10}));
        add(new GamePerk("Speed_Boost", "Ускорение", "Вы получаете эффект скорости I на %d секунд при старте.", Rarity.RARE, Material.BREWING_STAND_ITEM, 5, new int[]{0, 5000, 15000, 20000, 25000, 15, 17, 19, 22, 25}));
        add(new GamePerk("Resistance_Boost", "Сопротивление", "Вы получаете эффект сопротивления на %d секунд при старте.", Rarity.COMMON, Material.IRON_CHESTPLATE, 3, new int[]{0, 5000, 25000, 12, 16, 20}));
        add(new GamePerk("Instant_Smelting", "Мгновенная переплавка", "Теперь все в печке переплавляется мгновенно.", Rarity.COMMON, Material.FURNACE, 1, new int[]{0, 0}));
        add(new GamePerk("CoolRandom", "Счастливчик", "Минимально возможный уровень случайно выбираемых классов увеличен до %d.", Rarity.LEGENDARY, Material.DRAGON_EGG, 2, new int[]{0, 50000, 2, 3}));
    }

    private static void add(GamePerk gp) {
        PERKS.put(gp.getName(), gp);
    }

    public static Collection<GamePerk> getPerks() {
        return PERKS.values();
    }

    public static GamePerk getPerk(String name) {
        return (GamePerk) PERKS.get(name);
    }
}