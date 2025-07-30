package xiao.battleroyale.data.io;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.data.AbstractDataManager;

public class TempDataManager extends AbstractDataManager implements IGameManager {

    public static final String TEMP_DATA_SUB_PATH = "temp";

    private static class TempDataManagerHolder {
        private static final TempDataManager INSTANCE = new TempDataManager();
    }

    public static TempDataManager get() {
        return TempDataManagerHolder.INSTANCE;
    }

    private TempDataManager() {
        this.reloadData();
    }

    @Override
    protected String getSubPath() {
        return TEMP_DATA_SUB_PATH;
    }

    public void saveTempData() {
        super.saveData();
    }

    public void clearTempData() {
        super.clearData();
    }

    @Override
    public boolean isPreparedForGame() {
        return true;
    }
    @Override
    public void initGame(ServerLevel serverLevel) {
        ;
    }
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        ;
    }

    /**
     * 开始游戏时立即异步写入配置
     * 保证上一次游戏添加的临时数据能够重新读取
     */
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        saveTempData();
        return true;
    }

    @Override
    public void onGameTick(int gameTime) {
        ;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        ;
    }
}