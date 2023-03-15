package net.villenium.skywars;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.command.CommandManager;
import net.villenium.skywars.enums.PluginMode;
import net.villenium.skywars.handler.LobbyHandler;
import net.villenium.skywars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.villenium.skywars.handler.GlobalHandler;

public class SkyWars extends JavaPlugin {

    @Getter
    private static SkyWars instance;

    @Getter
    private PlayerManager playerManager;

    @Getter
    public static PluginMode pluginMode;

    @Override
    public void onEnable() {
        instance = this;
        playerManager = new PlayerManager();
        playerManager.initialize();
        registerCommands();
        if (!this.getConfig().isSet("plugin-mode")) {
            this.getConfig().set("plugin-mode", "lobby");
            saveConfig();
        }
        pluginMode = PluginMode.valueOf(this.getConfig().getString("plugin-mode").toUpperCase());

        if(pluginMode == PluginMode.GAME) {

        } else {
            Bukkit.getPluginManager().registerEvents(new LobbyHandler(), this);
        }
        Bukkit.getPluginManager().registerEvents(new GlobalHandler(), this);
    }

    private void registerCommands() {
        CommandManager manager = GameApi.getCommandManager();

    }
}
