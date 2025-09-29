package xiao.battleroyale.common.game.loot;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.AbstractGameManager;

public class GameInventoryManager extends AbstractGameManager {

    private static class GameInventoryManagerHolder {
        private static final GameInventoryManager INSTANCE = new GameInventoryManager();
    }

    public static GameInventoryManager get() {
        return GameInventoryManagerHolder.INSTANCE;
    }

    private GameInventoryManager() {
    }

    public static void init() {
        ;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {

    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        return false;
    }

    @Override
    public void onGameTick(int gameTime) {

    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {

    }
}