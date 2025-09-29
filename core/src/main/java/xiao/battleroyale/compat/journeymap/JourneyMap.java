package xiao.battleroyale.compat.journeymap;

import xiao.battleroyale.compat.AbstractCompatMod;

public class JourneyMap extends AbstractCompatMod {

    @Override
    public String getModId() {
        return "journeymap";
    }

    private static class JourneyMapHolder {
        private static final JourneyMap INSTANCE = new JourneyMap();
    }

    public static JourneyMap get() {
        return JourneyMapHolder.INSTANCE;
    }

    private JourneyMap() {}

    @Override
    protected void onModLoaded() throws Exception {
        ;
    }

    protected static boolean registered = false;

    public static void register() {
        JMEventHandler.register();
    }

    public static void unregister() {
        JMEventHandler.unregister();
    }

    protected static boolean shouldDisplayZone = true;

    public static void setJourneyMapConfig(boolean enableJourneyMap) {
        shouldDisplayZone = enableJourneyMap;
        if (get().isLoaded()) {
            if (shouldDisplayZone) {
                register();
            } else {
                unregister();
            }
        }
    }
}
