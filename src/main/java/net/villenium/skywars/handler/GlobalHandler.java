package net.villenium.skywars.handler;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.user.User;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.os.packetwrapper.WrapperPlayServerPlayerInfo;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.Task;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Collections;

public class GlobalHandler implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Shard.processQuitEvent(e.getPlayer());
        SkyWars.getInstance().getPlayerManager().getObjectPool().save(e.getPlayer().getName(), true);
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        e.getPlayer().sendMessage(ChatUtil.colorize("&fТы на шарде &a" + GamePlayer.wrap(e.getPlayer()).getShard().getId()));
    }
}
