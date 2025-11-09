package xiao.battleroyale.common.game;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.IGameSubManager;

public abstract class AbstractGameManager implements IGameSubManager {

    protected boolean configPrepared = false;
    protected boolean ready = false;

    @Override
    public boolean isConfigPrepared() {
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

    @Override
    public boolean registerGameEventHandler() {
        return true;
    }

    @Override
    public boolean unregisterGameEventHandler() {
        return true;
    }
}
