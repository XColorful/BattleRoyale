package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;

public abstract class AbstractGameManagerData {

    protected final String dataName;
    protected boolean locked = false;

    public AbstractGameManagerData(String dataName) {
        this.dataName = dataName;
    }

    protected void lockData() {
        this.locked = true;
        BattleRoyale.LOGGER.debug("{} locked", dataName);
    }

    protected void unlockData() {
        this.locked = false;
        BattleRoyale.LOGGER.debug("{} unlocked", dataName);
    }

    public abstract void clear();

    public abstract void startGame();

    public abstract void endGame();
}
