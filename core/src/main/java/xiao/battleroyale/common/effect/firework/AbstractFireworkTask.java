package xiao.battleroyale.common.effect.firework;

import net.minecraft.server.level.ServerLevel;

public class AbstractFireworkTask {

    protected ServerLevel serverLevel;
    protected long worldTime;
    protected int remainingAmount;
    protected int interval;
    protected int currentDelay;
    protected float vRange;
    protected float hRange;

    public ServerLevel getServerLevel() { return serverLevel; }
    public long getWorldTime() { return worldTime; }
    public int getRemainingAmount() { return remainingAmount; }

    public int getInterval() { return interval; }
    public int getCurrentDelay() { return currentDelay; }
    public float getVerticalRange() { return vRange; }
    public float getHorizontalRange() { return hRange; }

    public AbstractFireworkTask(ServerLevel serverLevel, int amount, int interval, float vRange, float hRange) {
        this.serverLevel = serverLevel;
        this.worldTime = serverLevel.getGameTime();
        this.remainingAmount = amount;
        this.interval = interval;
        this.currentDelay = interval;
        this.vRange = vRange;
        this.hRange = hRange;
    }
}
