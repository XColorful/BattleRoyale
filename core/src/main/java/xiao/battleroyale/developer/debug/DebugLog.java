package xiao.battleroyale.developer.debug;

import xiao.battleroyale.BattleRoyale;

public class DebugLog {

    private static class DebugLogHolder {
        private static final DebugLog INSTANCE = new DebugLog();
    }

    public static DebugLog get() {
        return DebugLogHolder.INSTANCE;
    }

    private DebugLog() {}

    public void logError(String msg) {
        BattleRoyale.LOGGER.error("Debug[Error] {}: {}", System.nanoTime(), msg);
    }
    public void logErrorLocal(String msg) {
        logError(msg);
    }

    public void logWarn(String msg) {
        BattleRoyale.LOGGER.warn("Debug[Warn] {}: {}", System.nanoTime(), msg);
    }
    public void logWarnLocal(String msg) {
        logWarn(msg);
    }

    public void logInfo(String msg) {
        BattleRoyale.LOGGER.info("Debug[Info] {}: {}", System.nanoTime(), msg);
    }
    public void logInfoLocal(String msg) {
        logInfo(msg);
    }

    public void logDebug(String msg) {
        BattleRoyale.LOGGER.debug("Debug[Debug] {}: {}", System.nanoTime(), msg);
    }
    public void logDebugLocal(String msg) {
        logDebug(msg);
    }
}
