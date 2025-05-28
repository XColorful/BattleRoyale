package xiao.battleroyale.api.game;

import net.minecraft.server.level.ServerLevel;

public interface IGameManager {

    void initGameConfig(ServerLevel serverLevel);

    boolean isPreparedForGame();

    void initGame(ServerLevel serverLevel);

    boolean isReady();

    boolean startGame(ServerLevel serverLevel);

    void stopGame(ServerLevel serverLevel);
}
