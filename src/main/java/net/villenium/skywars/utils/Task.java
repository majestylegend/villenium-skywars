package net.villenium.skywars.utils;

import net.villenium.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public abstract class Task extends BukkitRunnable {
    public static final HashMap<String, Task> tasks;

    static {
        tasks = new HashMap<>();
    }

    private final String name;
    private final JavaPlugin plugin;
    private int periods;
    private int delay;
    private int period;

    public Task(final JavaPlugin plugin, final String name, final int periods, final int delayInMilliseconds, final int periodInMilliseconds) {
        if (delayInMilliseconds != 0 && delayInMilliseconds < 50) {
            throw new IllegalArgumentException("Delay time must be 0 or not less than 50ms!");
        }
        if (periodInMilliseconds < 50) {
            throw new IllegalArgumentException("Period time must be not less than 50ms!");
        }
        this.name = name;
        this.plugin = plugin;
        if (periods == 0) {
            this.periods = -1;
        }
        this.periods = periods;
        this.delay = delayInMilliseconds / 50;
        this.period = periodInMilliseconds / 50;
        this.runTaskTimer(plugin, this.delay, this.period);
        Task.tasks.put(name, this);
    }

    public static Task getTask(final String name) {
        return Task.tasks.get(name);
    }

    public static void schedule(final Runnable r) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), r);
    }

    public static void schedule(final Runnable r, final long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyWars.getInstance(), r, delay);
    }

    public static void schedule(final Runnable r, final long delay, final long period) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkyWars.getInstance(), r, delay, period);
    }

    public static void runAsync(final Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(SkyWars.getInstance(), r);
    }

    public abstract void onTick();

    public void run() {
        if (this.periods > 0) {
            --this.periods;
        }
        this.onTick();
        if (this.periods == 0) {
            this.cancel();
        }
    }

    public int getPeriods() {
        return this.periods;
    }

    public void setPeriods(final int periods) {
        this.periods = periods;
    }

    public int getDelayInTicks() {
        return this.delay;
    }

    public int getPeriodInTicks() {
        return this.period;
    }

    public String getName() {
        return this.name;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public void cancel() {
        super.cancel();
        Task.tasks.remove(this.getName());
    }
}
