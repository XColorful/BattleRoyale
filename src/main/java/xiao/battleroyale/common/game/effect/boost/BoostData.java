package xiao.battleroyale.common.game.effect.boost;

import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class BoostData {

    public static final int BOOST_LEVEL_4 = 0xAAd46f16;
    public static final int BOOST_LEVEL_3 = 0xAAd7831e;
    public static final int BOOST_LEVEL_2 = 0xAAe1a31c;
    public static final int BOOST_LEVEL_1 = 0xAAe8c625;
    public static final int BOOST_LIMIT = 6000;

    public final UUID uuid;
    public ServerLevel level;
    private int boost = 0;
    public int boost() { return boost; }

    protected int healCooldown = 0;
    protected int effectCooldown = 0;
    protected int syncCooldown = 0;

    public BoostData(UUID entityUUID, ServerLevel level) {
        this.uuid = entityUUID;
        this.level = level;
    }

    protected int setBoost(int boost) {
        return this.boost = Math.max(Math.min(boost, BOOST_LIMIT), 0);
    }

    protected int addBoost(int amount) {
        return setBoost(this.boost + Math.max(amount, 0));
    }

    protected int dropBoost() {
        return setBoost(this.boost - 1);
    }

    public static double getBoostPercentage(int boost) {
        return (double) boost / BOOST_LIMIT;
    }

    public static final int BOOST_LV4 = 4;
    public static final int BOOST_LV3 = 3;
    public static final int BOOST_LV2 = 2;
    public static final int BOOST_LV1 = 1;
    public static final int BOOST_LV0 = 0;

    public static int getBoostLevel(int boost) {
        if (boost >= 5400) { // 90%
            return BOOST_LV4;
        } else if (boost >= 3600) { // 60%
            return BOOST_LV3;
        } else if (boost >= 1200) { // 20%
            return BOOST_LV2;
        } else if (boost > 0) { // 0%
            return BOOST_LV1;
        } else {
            return BOOST_LV0;
        }
    }
}
