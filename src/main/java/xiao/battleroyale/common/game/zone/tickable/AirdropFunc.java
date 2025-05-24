package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AirdropFunc extends AbstractSimpleFunc {


    public AirdropFunc(int moveDelay, int moveTime) {
        super(moveDelay, moveTime);
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<LivingEntity> gamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        super.initFunc(serverLevel, gamePlayers, gameZones, random);
    }

    @Override
    public void tick(ServerLevel serverLevel, List<LivingEntity> gamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {

    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.AIRDROP;
    }
}
