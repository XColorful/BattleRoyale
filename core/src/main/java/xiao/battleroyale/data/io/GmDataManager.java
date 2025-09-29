package xiao.battleroyale.data.io;

import xiao.battleroyale.data.AbstractDataManager;

public class GmDataManager extends AbstractDataManager {

    protected static final String GM_DATA_SUB_PATH = "gm";

    private static class GmDataManagerHolder {
        private static final GmDataManager INSTANCE = new GmDataManager();
    }

    public static GmDataManager get() {
        return GmDataManagerHolder.INSTANCE;
    }

    private GmDataManager() {
        this.reloadData();
    }

    @Override
    protected String getSubPath() {
        return GM_DATA_SUB_PATH;
    }

    @Override
    public void saveData() {
        // 游戏运行后不提供写回方式
    }
}
