package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameManager implements IGameManager {

    protected boolean configPrepared = false;
    protected boolean ready = false;

    @Override
    public boolean isPreparedForGame() {
        return this.configPrepared;
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
