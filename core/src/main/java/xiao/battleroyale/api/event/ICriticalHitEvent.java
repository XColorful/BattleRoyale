package xiao.battleroyale.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

public interface ICriticalHitEvent extends IEvent {

    Player getEntity();

    Entity getTarget();

    float getDamageMultiplier();

    void setDamageMultiplier(float multiplier);

    boolean isCriticalHit();

    void setCriticalHit(boolean isCriticalHit);

    boolean isVanillaCritical();

    /**
     * forge1.20.1-1.21.1, neoforge1.20.2-1.20.4 均没有这个方法
     */
    @ApiStatus.AvailableSince("neoforge1.21.1")
    void setDisableSweep(boolean disableSweep);

    /**
     * forge1.20.1-1.21.1, neoforge1.20.2-1.20.4 均没有这个方法
     */
    @ApiStatus.AvailableSince("neoforge1.21.1")
    boolean isDisableSweep();
}
