package net.villenium.skywars.commands;

import net.villenium.game.api.command.*;
import net.villenium.game.api.command.access.CommandAccess;
import net.villenium.game.api.command.access.CommandAccessResult;
import net.villenium.game.api.user.permission.PermissionGroup;
import net.villenium.game.api.user.permission.UserPermission;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GamePhase;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.command.CommandSender;

@Command("forcestart")
@Description("Принудительный запуск игры.")
@Usage("/forcestart")
@Aliases("fs")
public class CommandForceStart implements CommandAccess {

    @Override
    public CommandAccessResult hasAccess(UserPermission permission) {
        return permission.isHeadAdministrator() ? null : new CommandAccessResult(PermissionGroup.HEAD_ADMIN);
    }

    @CommandHandler
    public void handle(CommandSender sender, String[] args) {
        if (GamePlayer.wrap(sender.getName()).getShard() instanceof GameShard) {
            sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cДействие возможно только на игровом сервере!"));
        } else {
            GameShard game = (GameShard) GamePlayer.wrap(sender.getName()).getShard();
            if (game == null) {
                sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы не находитесь ни на одном из игровых шардов!"));
            } else {
                sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&aЗапускаю игру на вашем шарде (&b%s&a)!", new Object[]{game.getId()}));
                game.switchPhase(GamePhase.PREGAME);
            }
        }
    }

}
