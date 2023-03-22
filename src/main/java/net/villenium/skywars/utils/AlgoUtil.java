package net.villenium.skywars.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author RinesThaix
 */
@SuppressWarnings("unchecked")
public class AlgoUtil {

    @Getter
    private final static Random random = new Random();
    private final static Vec minA = new Vec(-.5, 0, -.5), maxA = new Vec(.5, 1.67, .5);
    private final static double eps = 1e-3;

    public static Vector getTrajectory(Entity from, Entity to) {
        return getTrajectory(from.getLocation().toVector(), to.getLocation().toVector());
    }

    public static Vector getTrajectory(Location from, Location to) {
        return getTrajectory(from.toVector(), to.toVector());
    }

    public static Vector getTrajectory(Vector from, Vector to) {
        return to.subtract(from).normalize();
    }

    public static Vector getTrajectory2d(Entity from, Entity to) {
        return getTrajectory2d(from.getLocation().toVector(), to.getLocation().toVector());
    }

    public static Vector getTrajectory2d(Location from, Location to) {
        return getTrajectory2d(from.toVector(), to.toVector());
    }

    public static Vector getTrajectory2d(Vector from, Vector to) {
        return to.subtract(from).setY(0).normalize();
    }

    public static boolean hasSight(Location from, Player to) {
        return (hasSight(from, to.getLocation())) || (hasSight(from, to.getEyeLocation()));
    }

    public static boolean hasSight(Location from, Location to) {
        Location cur = new Location(from.getWorld(), from.getX(), from.getY(), from.getZ());
        double rate = 0.1d;
        Vector vec = getTrajectory(from, to).multiply(0.1d);
        while (offset(cur, to) > rate) {
            cur.add(vec);
            if (!BlockUtil.isAirFoliage(cur.getBlock())) {
                return false;
            }
        }
        return true;
    }

    public static int r(int i) {
        return random.nextInt(i);
    }

    public static int r() {
        return random.nextInt();
    }

    public static double offset2d(Entity a, Entity b) {
        return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset2d(Location a, Location b) {
        return offset2d(a.toVector(), b.toVector());
    }

    public static double offset2d(Vector a, Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }

    public static double offset(Entity a, Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset(Location a, Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static double offset(Vector a, Vector b) {
        return a.subtract(b).length();
    }

    public static <T extends Entity> Collection<T> getNearbyEntities(Location l, Class<T> type, int radius) {
        World w = l.getWorld();
        Chunk min = l.clone().add(-radius, 0, -radius).getChunk();
        Chunk max = l.clone().add(radius, 0, radius).getChunk();
        int radius2 = radius * radius;
        if (type == Player.class) {
            int chunks = (max.getX() - min.getX() + 1) * (max.getZ() - min.getZ() + 1);
            if (chunks >= Bukkit.getOnlinePlayers().size()) {
                return (Collection<T>) Bukkit.getOnlinePlayers().stream().filter(e -> e.getWorld() == w && e.getLocation().distanceSquared(l) <= radius2).collect(Collectors.toSet());
            }
        }
        Collection<T> result = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int z = min.getZ(); z <= max.getZ(); ++z) {
                Chunk chunk = w.getChunkAt(x, z);
                for (Entity entity : chunk.getEntities()) {
                    if (type.isAssignableFrom(entity.getClass()) && entity.getLocation().distanceSquared(l) <= radius2) {
                        result.add((T) entity);
                    }
                }
            }
        }
        return result;
    }

    private static boolean hasOnLine(Vector st, Vector en, Vector po) {
        Vec start = new Vec(st), end = new Vec(en), pos = new Vec(po);
        Vec min = pos.add(minA), max = pos.add(maxA);
        Vec d = end.subtract(start).multiply(.5),
                e = max.subtract(min).multiply(.5),
                c = start.add(d).subtract(min.add(max).multiply(.5)),
                ad = new Vec(Math.abs(d.x), Math.abs(d.y), Math.abs(d.z));
        if (Math.abs(c.getX()) > e.getX() + ad.getX()) {
            return false;
        }
        if (Math.abs(c.getY()) > e.getY() + ad.getY()) {
            return false;
        }
        if (Math.abs(c.getZ()) > e.getZ() + ad.getZ()) {
            return false;
        }
        if (Math.abs(d.getY() * c.getZ() - d.getZ() * c.getY()) > e.getY() * ad.getZ() + e.getZ() * ad.getY() + eps) {
            return false;
        }
        if (Math.abs(d.getZ() * c.getX() - d.getX() * c.getZ()) > e.getZ() * ad.getX() + e.getX() * ad.getZ() + eps) {
            return false;
        }
        return !(Math.abs(d.getX() * c.getY() - d.getY() * c.getX()) > e.getX() * ad.getY() + e.getY() * ad.getX() + eps);
    }

    public static boolean hasInsideOfTrajectory(Location a, Location b, Location point) {
        return hasOnLine(a.toVector(), b.toVector(), point.toVector());
    }

    public static boolean hasInLineOfSight(Location from, Location to) {
        return hasOnLine(from.toVector(), from.toVector().add(from.getDirection().multiply(16)), to.toVector());
    }

    public static boolean hasInLineOfSight(LivingEntity le1, LivingEntity le2) {
        if (le1 == le2) {
            return false;
        }
        return hasInLineOfSight(le1.getEyeLocation(), le2.getLocation());
    }

    public static LivingEntity getEntityInLightOfSight(LivingEntity le, int max_distance) {
        Location loc = le.getLocation();
        return getNearbyEntities(loc, LivingEntity.class, max_distance).stream()
                .filter(le2 -> hasInLineOfSight(le, le2))
                .min((a, b) -> a.getLocation().distance(loc) < b.getLocation().distance(loc) ? -1 : 1)
                .orElse(null);
    }

    public static Set<Player> getPlayersInLightOfSight(LivingEntity le, int max_distance) {
        return getEntitiesInLightOfSight(le, Player.class, max_distance);
    }

    public static <T extends LivingEntity> Set<T> getEntitiesInLightOfSight(LivingEntity le, Class<T> type, int max_distance) {
        return getNearbyEntities(le.getLocation(), type, max_distance).stream()
                .filter(le2 -> hasInLineOfSight(le, le2))
                .collect(Collectors.toSet());
    }

    public static <T> List<T> newArrayList(T... objects) {
        return Lists.newArrayList(objects);
    }

    public static <T> Set<T> newHashSet(T... objects) {
        return Sets.newHashSet(objects);
    }

    public static Map newHashMap(Object... objects) {
        Map map = new HashMap<>();
        for (int i = 0; i < objects.length; ++i) {
            map.put(objects[i++], objects[i]);
        }
        return map;
    }

    public static final Vector rotateAroundAxisX(Vector v, float angle) {
        double y, z, cos, sin;
        cos = MathHelper.cos(angle);
        sin = MathHelper.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        v.setY(y);
        v.setZ(z);
        return v;
    }

    public static final Vector rotateAroundAxisY(Vector v, float angle) {
        double x, z, cos, sin;
        cos = MathHelper.cos(angle);
        sin = MathHelper.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        v.setX(x);
        v.setZ(z);
        return v;
    }

    public static final Vector rotateAroundAxisZ(Vector v, float angle) {
        double x, y, cos, sin;
        cos = MathHelper.cos(angle);
        sin = MathHelper.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        v.setX(x);
        v.setY(y);
        return v;
    }

    public static final Vector rotateVector(Vector v, float angleX, float angleY, float angleZ) {
        if (angleX != 0) {
            rotateAroundAxisX(v, angleX);
        }
        if (angleY != 0) {
            rotateAroundAxisY(v, angleY);
        }
        if (angleZ != 0) {
            rotateAroundAxisZ(v, angleZ);
        }
        return v;
    }

    private static class Vec {

        @Getter
        private final float x, y, z;

        public Vec(Vector v) {
            this(v.getX(), v.getY(), v.getZ());
        }

        public Vec(Location loc) {
            this(loc.getX(), loc.getY(), loc.getZ());
        }

        public Vec(double x, double y, double z) {
            this.x = (float) x;
            this.y = (float) y;
            this.z = (float) z;
        }

        public Vec() {
            this.x = this.y = this.z = 0F;
        }

        public Vec add(Vec v) {
            return new Vec(x + v.x, y + v.y, z + v.z);
        }

        public Vec subtract(Vec v) {
            return new Vec(x - v.x, y - v.y, z - v.z);
        }

        public Vec multiply(double value) {
            return new Vec(x * value, y * value, z * value);
        }

    }

}
