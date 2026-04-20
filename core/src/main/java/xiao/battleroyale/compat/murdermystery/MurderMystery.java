package xiao.battleroyale.compat.murdermystery;

public class MurderMystery {

    public String getModId() {
        return "murdermystery";
    }

    private static class MurderMysteryHolder {
        private static final MurderMystery INSTANCE = new MurderMystery();
    }

    public static MurderMystery get() {
        return MurderMysteryHolder.INSTANCE;
    }
}
