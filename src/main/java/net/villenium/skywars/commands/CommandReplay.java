package net.villenium.skywars.commands;

import net.villenium.game.api.command.Command;
import net.villenium.game.api.command.CommandHandler;
import net.villenium.game.api.command.Description;
import net.villenium.game.api.command.Usage;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.menu.MenuItemHandler;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.telegram.telegrambots.meta.api.objects.games.Game;

@Command("replay")
@Description("Начать новую игру.")
@Usage("/replay")
public class CommandReplay {

    @CommandHandler
    public void handle(CommandSender sender, String[] args) {
        if(args.length == 0) return;
        MenuItemHandler.findGame(Bukkit.getPlayer(sender.getName()), GameType.valueOf(args[0]));
    }

}
