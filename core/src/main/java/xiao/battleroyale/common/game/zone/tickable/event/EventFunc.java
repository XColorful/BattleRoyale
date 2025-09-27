package xiao.battleroyale.common.game.zone.tickable.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.zone.CustomZoneEvent;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager.ZoneTickContext;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

public class EventFunc extends AbstractEventFunc {

    public EventFunc(int moveDelay, int moveTime, int tickFreq, int tickOffset,
                     String protocol, @NotNull CompoundTag tag) {
        super(moveDelay, moveTime, tickFreq, tickOffset, protocol, tag);
    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.EVENT;
    }

    @Override
    public void funcTick(ZoneTickContext zoneTickContext) {
        GameManager gameManager = GameManager.get();
        for (GamePlayer gamePlayer : zoneTickContext.gamePlayers) {
            if (zoneTickContext.spatialZone.isWithinZone(gamePlayer.getLastPos(), zoneTickContext.progress)) {
                LivingEntity livingEntity = (LivingEntity) zoneTickContext.serverLevel.getEntity(gamePlayer.getPlayerUUID());
                MinecraftForge.EVENT_BUS.post(new CustomZoneEvent(gameManager, zoneTickContext, this.protocol, this.tag, gamePlayer, livingEntity));
            }
        }
    }
}
