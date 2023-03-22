package net.villenium.skywars;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.command.CommandManager;
import net.villenium.skywars.commands.CommandForceStart;
import net.villenium.skywars.commands.CommandLobby;
import net.villenium.skywars.enums.GameType;
import net.villenium.skywars.game.CageManager;
import net.villenium.skywars.game.GameClassManager;
import net.villenium.skywars.game.GameMap;
import net.villenium.skywars.game.GamePerkManager;
import net.villenium.skywars.game.usables.ItemsManager;
import net.villenium.skywars.handler.GameHandler;
import net.villenium.skywars.handler.GlobalHandler;
import net.villenium.skywars.handler.LobbyHandler;
import net.villenium.skywars.player.PlayerManager;
import net.villenium.skywars.shards.GameShard;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

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
        initializeGame();
        new LobbyShard("lobby-1");
        new LobbyShard("lobby-2");
    }

    @Override
    public void onDisable() {
        Shard.invalidateAll();
    }

    public void initializeGame() {
        CageManager.init();
        GameClassManager.init();
        GamePerkManager.init();
    }

    private void registerHandlers() {
        //Обрабатывается на игровом и лобби шардах
        Bukkit.getPluginManager().registerEvents(new GlobalHandler(), this);
        //Обрабатывается только в лобби шарде
        Bukkit.getPluginManager().registerEvents(new LobbyHandler(), this);
        //Обрабатывается тольно на игровых шардах
        Bukkit.getPluginManager().registerEvents(new GameHandler(), this);

        Bukkit.getPluginManager().registerEvents(new ItemsManager(), this);
        Bukkit.getPluginManager().registerEvents(new Cooldowns(), this);
    }

    private void registerCommands() {
        CommandManager manager = GameApi.getCommandManager();
        manager.registerCommand(CommandLobby.class);
        manager.registerCommand(CommandForceStart.class);
    }
}
