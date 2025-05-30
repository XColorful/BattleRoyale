package xiao.battleroyale.api.game;

import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public interface IGameManager {

    void initGameConfig(ServerLevel serverLevel);

    boolean isPreparedForGame();

    void initGame(ServerLevel serverLevel);

    boolean isReady();

    boolean startGame(ServerLevel serverLevel);

    void onGameTick(int gameTime);

    void stopGame(@Nullable ServerLevel serverLevel);
}
