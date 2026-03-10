package xiao.battleroyale.compat.fabric.compat.journeymap;

import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.compat.journeymap.JMPolygonOverlay;

public class JmApi implements IJmApi {

    private static class JmApiHolder {
        private static final JmApi INSTANCE = new JmApi();
    }

    public static IJmApi get() {
        return JmApiHolder.INSTANCE;
    }

    private JmApi() {}

    @Override
    public void removeAll(String modId) {
        // 空实现
    }

    @Override
    public void show(JMPolygonOverlay jmPolygonOverlay) {
        // 空实现
    }
}