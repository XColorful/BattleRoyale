package xiao.battleroyale.api.common;

import xiao.battleroyale.BattleRoyale;

public interface ISideOnly {

    default boolean clientSideOnly() {
        return false;
    }
    default boolean serverSideOnly() {
        return false;
    }
    default boolean inProperSide() {
        return inProperSide(BattleRoyale.getMcSide());
    }
    default boolean inProperSide(McSide mcSide) {
        if (clientSideOnly() && mcSide == McSide.DEDICATED_SERVER) {
            return false;
        } else return !serverSideOnly() || mcSide != McSide.CLIENT;
    }
}
