package xiao.battleroyale.common.game.zone.tickable;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class AirdropFunc extends AbstractSimpleFunc {


    public AirdropFunc(int moveDelay, int moveTime) {
        super(moveDelay, moveTime);
    }

    @Override
    public void initFunc(ServerLevel serverLevel, List<UUID> playerIdList, Map<Integer, IGameZone> gameZones, Supplier<Float> random) {
        super.initFunc(serverLevel, playerIdList, gameZones, random);
    }

    @Override
    public void tick(ServerLevel serverLevel, List<UUID> playerIdList, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {

    }

    @Override
    public ZoneFuncType getFuncType() {
        return ZoneFuncType.AIRDROP;
    }
}
