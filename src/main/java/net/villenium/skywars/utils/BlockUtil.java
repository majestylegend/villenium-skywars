package net.villenium.skywars.utils;

import net.villenium.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;

/**
 * @author RinesThaix
 */
public class BlockUtil {

    private final static Set<Byte> blockAirFoliageSet = new HashSet<>();

    public static Location strToLoc(String s) {
        String[] spl = s.split(" ");
        World w = Bukkit.getWorld(spl[0]);
        if (w == null) {
            Bukkit.createWorld(new WorldCreator(spl[0]));
        }
        w = Bukkit.getWorld(spl[0]);
        double x = Double.parseDouble(spl[1]);
        double y = Double.parseDouble(spl[2]);
        double z = Double.parseDouble(spl[3]);
        float yaw = Float.parseFloat(spl[4]);
        float pitch = Float.parseFloat(spl[5]);
        Location l = new Location(w, x, y, z);
        l.setYaw(yaw);
        l.setPitch(pitch);
        return l;
    }

    public static Location strToLocReplaceWorld(String s, World world) {
        String[] spl = s.split(" ");
        double x = Double.parseDouble(spl[1]);
        double y = Double.parseDouble(spl[2]);
        double z = Double.parseDouble(spl[3]);
        float yaw = Float.parseFloat(spl[4]);
        float pitch = Float.parseFloat(spl[5]);
        Location l = new Location(world, x, y, z);
        l.setYaw(yaw);
        l.setPitch(pitch);
        return l;
    }

    public static String locToStr(Location l) {
        StringBuilder sb = new StringBuilder();
        sb.append(l.getWorld().getName()).append(" ")
                .append(l.getX()).append(" ")
                .append(l.getY()).append(" ")
                .append(l.getZ()).append(" ")
                .append(l.getYaw()).append(" ")
                .append(l.getPitch());
        return sb.toString();
    }

    public static void removeMeta(Block block, String key) {
        block.removeMetadata(key, SkyWars.getInstance());
    }

    public static void setMeta(Block block, String key, Object value) {
        block.setMetadata(key, new FixedMetadataValue(SkyWars.getInstance(), value));
    }

    public static Object getMeta(Block block, String key) {
        return block.getMetadata(key).get(0).value();
    }

    public static boolean hasMeta(Block block, String key) {
        return block.hasMetadata(key);
    }

    @SuppressWarnings("deprecation")
    public static boolean isAirFoliage(Block block) {
        if (block == null) {
            return false;
        }
        return isAirFoliage(block.getTypeId());
    }

    public static boolean isAirFoliage(int block) {
        return airFoliage((byte) block);
    }

    public static boolean airFoliage(byte block) {
        if (blockAirFoliageSet.isEmpty()) {
            blockAirFoliageSet.add((byte) 0);
            blockAirFoliageSet.add((byte) 6);
            blockAirFoliageSet.add((byte) 31);
            blockAirFoliageSet.add((byte) 32);
            blockAirFoliageSet.add((byte) 37);
            blockAirFoliageSet.add((byte) 38);
            blockAirFoliageSet.add((byte) 39);
            blockAirFoliageSet.add((byte) 40);
            blockAirFoliageSet.add((byte) 51);
            blockAirFoliageSet.add((byte) 59);
            blockAirFoliageSet.add((byte) 104);
            blockAirFoliageSet.add((byte) 105);
            blockAirFoliageSet.add((byte) 115);
            blockAirFoliageSet.add((byte) -115);
            blockAirFoliageSet.add((byte) -114);
        }

        return blockAirFoliageSet.contains(block);
    }

}
