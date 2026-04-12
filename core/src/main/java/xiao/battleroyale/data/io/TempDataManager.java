package xiao.battleroyale.data.io;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.data.AbstractDataManager;

public class TempDataManager extends AbstractDataManager implements IGameSubManager {

    public static final String TEMP_DATA_SUB_PATH = "temp";

    private static class TempDataManagerHolder {
        private static final TempDataManager INSTANCE = new TempDataManager();
    }

    public static TempDataManager get() {
        return TempDataManagerHolder.INSTANCE;
    }

    public static final String _MANAGER_NAME = String.format("%s:TempDataManager", BattleRoyale.MOD_ID);
    @Override public String getManagerName() {
        return _MANAGER_NAME;
    }
    @Override public boolean registerGameEventHandler() {
        return false;
    }
    @Override public boolean unregisterGameEventHandler() {
        return false;
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
    public boolean isConfigPrepared() {
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