package xiao.battleroyale.data.io;

import xiao.battleroyale.data.AbstractDataManager;

public class DevDataManager extends AbstractDataManager {

    protected static final String DEV_DATA_SUB_PATH = "developer";

    private static class DevDataManagerHolder {
        private static final DevDataManager INSTANCE = new DevDataManager();
    }

    public static DevDataManager get() {
        return DevDataManagerHolder.INSTANCE;
    }

    private DevDataManager() {
        this.reloadData();
    }

    @Override
    protected String getSubPath() {
        return DEV_DATA_SUB_PATH;
    }

    public void saveDevData() {
        super.saveData();
    }
}
