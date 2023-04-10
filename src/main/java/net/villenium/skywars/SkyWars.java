package net.villenium.skywars;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.command.CommandManager;
import net.villenium.skywars.commands.*;
import net.villenium.skywars.game.CageManager;
import net.villenium.skywars.game.GameClassManager;
import net.villenium.skywars.game.GamePerkManager;
import net.villenium.skywars.game.KitSelector;
import net.villenium.skywars.game.usables.ItemsManager;
import net.villenium.skywars.handler.GameHandler;
import net.villenium.skywars.handler.GlobalHandler;
import net.villenium.skywars.handler.LobbyHandler;
import net.villenium.skywars.lobby.LobbyEngine;
import net.villenium.skywars.player.PlayerManager;
import net.villenium.skywars.shards.LobbyShard;
import net.villenium.skywars.shards.Shard;
import net.villenium.skywars.utils.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyWars extends JavaPlugin {

    @Getter
    private static SkyWars instance;
    @Getter
    SlimePlugin slimePlugin;
    @Getter
    SlimeLoader slimeLoader;
    @Getter
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimePlugin.getLoader("file");
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
        KitSelector.init();
        LobbyEngine.init();
        Shard.init();
    }

    private void registerHandlers() {
        Bukkit.getPluginManager().registerEvents(new GlobalHandler(), this);
        Bukkit.getPluginManager().registerEvents(new LobbyHandler(), this);
        Bukkit.getPluginManager().registerEvents(new GameHandler(), this);
        Bukkit.getPluginManager().registerEvents(new ItemsManager(), this);
        Bukkit.getPluginManager().registerEvents(new Cooldowns(), this);
    }

    private void registerCommands() {
        CommandManager manager = GameApi.getCommandManager();
        manager.registerCommand(CommandLobby.class);
        manager.registerCommand(CommandForceStart.class);
        manager.registerCommand(CommandDeathmatch.class);
        manager.registerCommand(CommandSpawn.class);
        manager.registerCommand(CommandStress.class);
        manager.registerCommand(CommandReplay.class);
    }
}
