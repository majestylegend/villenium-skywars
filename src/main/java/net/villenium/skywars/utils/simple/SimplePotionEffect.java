package net.villenium.skywars.utils.simple;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SimplePotionEffect extends PotionEffect {
    public SimplePotionEffect(final PotionEffectType type, final int level) {
        this(type, level, -1.0);
    }

    public SimplePotionEffect(final PotionEffectType type, final int level, final double durationInSeconds) {
        super(type, (durationInSeconds == -1.0) ? 120000 : ((int) (durationInSeconds * 20.0)), level - 1);
    }
}
