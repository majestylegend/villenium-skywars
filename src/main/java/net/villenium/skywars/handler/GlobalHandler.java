package net.villenium.skywars.handler;

import net.villenium.game.api.GameApi;
import net.villenium.game.api.user.User;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlobalHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Shard.processQuitEvent(e.getPlayer());
        SkyWars.getInstance().getPlayerManager().getObjectPool().save(e.getPlayer().getName(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent e) {
        Shard.processQuitEvent(e.getPlayer());
        SkyWars.getInstance().getPlayerManager().getObjectPool().save(e.getPlayer().getName(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockChange(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String msg = e.getMessage();
        Player p = e.getPlayer();
        User user = GameApi.getUserManager().get(p);
        Task.schedule(() -> {
            Shard.broadcastRaw(p, ChatUtil.colorize("%s&8: &r", user.getFullDisplayName()) + (user.getPermission().isAdministrator() ? ChatUtil.colorize(msg) : msg));
        });
    }
}
