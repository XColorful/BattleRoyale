package xiao.battleroyale.compat;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.compat.ICompatMod;

public abstract class AbstractCompatMod implements ICompatMod {

    protected final String MOD_ID;
    protected boolean hasChecked = false;
    protected boolean isLoaded = false;

    public AbstractCompatMod() {
        MOD_ID = getModId();
    }

    protected abstract void onModLoaded() throws Exception;

    @Override
    public void checkLoaded() {
        if (BattleRoyale.getMcRegistry().isModLoaded(MOD_ID)) {
            try {
                onModLoaded();
                isLoaded = true;
                BattleRoyale.LOGGER.debug("Detected {} is loaded", MOD_ID);
            } catch (Exception e) {
                isLoaded = false;
                BattleRoyale.LOGGER.debug("Failed to initialize compat for {}: {}", MOD_ID, e.toString());
            }
        } else {
            BattleRoyale.LOGGER.debug("Detected {} not loaded", MOD_ID);
        }
        hasChecked = true;
    }

    @Override
    public boolean isLoaded() {
        if (!hasChecked) {
            checkLoaded();
        }
        return isLoaded;
    }
}
