package xiao.battleroyale.compat.neoforge.compat.journeymap;

import xiao.battleroyale.compat.journeymap.JMPolygonOverlay;

public class JourneyMapPlugin {

    private static JourneyMapPlugin INSTANCE;

    public JourneyMapPlugin() {
        INSTANCE = this;
    }

    public static JourneyMapPlugin getInstance() {
        return INSTANCE;
    }

    public void removeAll(String modId) {
    }

    public void show(JMPolygonOverlay JMPolygonOverlay) {
    }
}
