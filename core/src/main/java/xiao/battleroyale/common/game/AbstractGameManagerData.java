package xiao.battleroyale.common.game;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.data.data.AbstractNameData;

public abstract class AbstractGameManagerData extends AbstractNameData {

    protected boolean locked = false;

    public AbstractGameManagerData(String dataName) {
        super(dataName);
    }

    protected void lockData() {
        this.locked = true;
        BattleRoyale.LOGGER.debug("{} locked", dataName);
    }

    protected void unlockData() {
        this.locked = false;
        BattleRoyale.LOGGER.debug("{} unlocked", dataName);
    }

    public abstract void startGame();

    public abstract void endGame();
}
