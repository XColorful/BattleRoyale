package xiao.battleroyale.compat.cbraddon;

public class CbrAddon {

    public String getModId() {
        return "cbraddon";
    }

    private static class CbrAddonHolder {
        private static final CbrAddon INSTANCE = new CbrAddon();
    }

    public static CbrAddon get() {
        return CbrAddonHolder.INSTANCE;
    }
}
