package net.villenium.skywars.commands;

import net.villenium.game.api.command.Command;
import net.villenium.game.api.command.CommandHandler;
import net.villenium.game.api.command.Description;
import net.villenium.game.api.command.Usage;
import net.villenium.game.api.util.ChatUtil;
import net.villenium.skywars.player.GamePlayer;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.command.CommandSender;

@Command("lobby")
@Description("Телепортироваться в лобби.")
@Usage("/lobby")
public class CommandLobby {

    @CommandHandler
    public void handle(CommandSender sender, String[] args) {
        if (GamePlayer.wrap(sender.getName()).getShard() instanceof LobbyShard) {
            sender.sendMessage(ChatUtil.prefixed("&6&lSkyWars", "&cВы уже подключены к лобби режима."));
            return;
        }
        GamePlayer.wrap(sender.getName()).moveToShard(Shard.getRandomLobby());
    }

}
