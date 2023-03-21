package net.villenium.skywars.player;

import lombok.Getter;
import net.villenium.game.api.GameApi;
import net.villenium.game.api.athena.AthenaStorage;
import net.villenium.game.api.athena.ObjectPool;

public class PlayerManager {

    private AthenaStorage<GamePlayer> PLAYERS = GameApi.getStorageManager().create("skywars_players", GamePlayer.class);
    @Getter
    private ObjectPool<GamePlayer> objectPool;

    public void initialize() {
        objectPool = PLAYERS.newObjectPool();
        objectPool.setDefaultObject(new GamePlayer(null, 0, 0, 0, 0, null));
    }

}