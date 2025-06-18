package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameManager implements IGameManager {

    public static String MOD_DATA_PATH = "battleroyale";

    protected boolean prepared = false;
    protected boolean ready = false;

    @Override
    public boolean isPreparedForGame() {
        return this.prepared;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        this.ready = true;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }
}
