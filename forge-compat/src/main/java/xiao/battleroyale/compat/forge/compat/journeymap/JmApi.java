package xiao.battleroyale.compat.forge.compat.journeymap;

import xiao.battleroyale.api.compat.journeymap.IJmApi;
import xiao.battleroyale.compat.journeymap.*;

public class JmApi implements IJmApi {

    private static class JmApiHolder {
        private static final JmApi INSTANCE = new JmApi();
    }

    public static IJmApi get() {
        return JmApiHolder.INSTANCE;
    }

    private JmApi() {}

    protected static boolean initialized = false; // JourneyMapPlugin类加载时会设置

    @Override
    public void removeAll(String modId) {
        if (initialized) {
            JourneyMapPlugin.getInstance().removeAll(modId);
        }
    }

    @Override
    public void show(JMPolygonOverlay JMPolygonOverlay) {
        if (initialized) {
            JourneyMapPlugin.getInstance().show(JMPolygonOverlay);
        }
    }
}
