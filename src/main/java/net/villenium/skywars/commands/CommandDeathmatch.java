package net.villenium.skywars.commands;

import net.villenium.game.api.command.*;
import net.villenium.game.api.command.access.CommandAccess;
import net.villenium.game.api.command.access.CommandAccessResult;
import net.villenium.game.api.user.permission.PermissionGroup;
import net.villenium.game.api.user.permission.UserPermission;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.SkyWars;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.utils.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Command("deathmatch")
@Description("Настройка локаций дэзматча.")
@Usage("/deathmatch")
@Aliases("dm")
public class CommandDeathmatch implements CommandAccess {

    @Override
    public CommandAccessResult hasAccess(UserPermission permission) {
        return permission.isHeadAdministrator() ? null : new CommandAccessResult(PermissionGroup.HEAD_ADMIN);
    }

    @CommandHandler
    public void handle(CommandSender sender, String[] args) {
        if (!(GamePlayer.wrap(sender.getName()).getShard() instanceof GameShard)) {
            sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cДействие возможно только на игровом сервере!"));
        } else {
            GameShard game = (GameShard) GamePlayer.wrap(sender.getName()).getShard();
            if (game == null) {
                sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы не находитесь ни на одном из игровых шардов!"));
            } else {
                String path = "gameMaps." + (game.getGameType().toString().contains("SOLO") ? "solo" : "team") + "." + game.getMap().getName() + ".deathmatchLocations";
                List<String> list = SkyWars.getInstance().getConfig().isSet(path) ? SkyWars.getInstance().getConfig().getStringList(path) : new ArrayList<>();
                Location location = Bukkit.getPlayer(sender.getName()).getLocation();
                location.setWorld(Bukkit.getWorld(game.getMap().getName()));
                list.add(BlockUtil.locToStr(location));
                SkyWars.getInstance().getConfig().set(path, list);
                SkyWars.getInstance().saveConfig();
                sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aНовая локация успешно добавлена!"));
            }
        }
    }

}
