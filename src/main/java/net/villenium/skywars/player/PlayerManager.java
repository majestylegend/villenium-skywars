package net.villenium.skywars.player;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.athena.AthenaStorage;
import net.villenium.game.api.athena.ObjectPool;
import net.villenium.game.api.athena.util.Athena;
import net.villenium.skywars.game.Cage;
import net.villenium.skywars.game.GameClass;
import net.villenium.skywars.game.GamePerk;
import net.villenium.skywars.utils.CageSerializer;
import net.villenium.skywars.utils.ClassSerializer;
import net.villenium.skywars.utils.PerkSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private AthenaStorage<GamePlayer> PLAYERS = GameApi.getStorageManager().create("skywars_players", Athena.getGsonBuilder().registerTypeHierarchyAdapter(GamePerk.class, new PerkSerializer()).registerTypeHierarchyAdapter(GameClass.class, new ClassSerializer()).registerTypeHierarchyAdapter(Cage.class, new CageSerializer()).create(), GamePlayer.class);
    @Getter
    private ObjectPool<GamePlayer> objectPool;

    public static Map<String, GamePlayer> cache = new HashMap<>();

    public void initialize() {
        objectPool = PLAYERS.newObjectPool();
        objectPool.setDefaultObject(new GamePlayer(null, new HashMap<>(), null, new ArrayList<>(), null, 0, 0, 0, 0, new HashMap<>()));
    }

}