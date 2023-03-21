package net.villenium.skywars;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.command.CommandManager;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.handler.GameHandler;
import net.villenium.skywars.handler.LobbyHandler;
import net.villenium.skywars.player.PlayerManager;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.villenium.skywars.handler.GlobalHandler;

import java.util.ArrayList;
import java.util.List;

public class SkyWars extends JavaPlugin {

    @Getter
    private static SkyWars instance;

    @Getter
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        playerManager = new PlayerManager();
        playerManager.initialize();
        registerCommands();
        registerHandlers();
        new LobbyShard("lobby-1");
        new LobbyShard("lobby-2");
    }

    @Override
    public void onDisable() {
        Shard.invalidateAll();
    }

    private void registerHandlers() {
        //Обрабатывается на игровом и лобби шардах
        Bukkit.getPluginManager().registerEvents(new GlobalHandler(), this);
        //Обрабатывается только в лобби шарде
        Bukkit.getPluginManager().registerEvents(new LobbyHandler(), this);
        //Обрабатывается тольно на игровых шардах
        Bukkit.getPluginManager().registerEvents(new GameHandler(), this);
    }

    private void registerCommands() {
        CommandManager manager = GameApi.getCommandManager();

    }
}
