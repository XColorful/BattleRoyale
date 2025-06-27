package xiao.battleroyale.common.message.zone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.network.message.ClientMessageZoneInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.*;
import java.util.function.Function;

public class ZoneMessageManager extends AbstractMessageManager<ZoneMessage> {

    private static class ZoneMessageManagerHolder {
        private static final ZoneMessageManager INSTANCE = new ZoneMessageManager();
    }

    public static ZoneMessageManager get() {
        return ZoneMessageManagerHolder.INSTANCE;
    }

    @Override
    protected void sendMessageToPlayers(List<ServerPlayer> players, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageZoneInfo message = new ClientMessageZoneInfo(nbtMessage);
        for (ServerPlayer player : players) {
            SendUtils.sendMessageToPlayer(player, message);
        }
    }

    @Override
    protected void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageZoneInfo message = new ClientMessageZoneInfo(nbtMessage);
        SendUtils.sendMessageToGamePlayers(gamePlayers, message, serverLevel);
    }

    @Override
    public void notifyNbtChange(int nbtId) {
        IGameZone gameZone = ZoneManager.get().getZoneById(nbtId);
        if (gameZone == null) {
            addNbtMessage(nbtId, null);
        } else {
            int gameTime = GameManager.get().getGameTime();
            int zoneDelay = gameZone.getZoneDelay();
            double shapeProgress = gameZone.getShapeProgress(gameTime, zoneDelay);
            addNbtMessage(nbtId, gameZone.toNBT(shapeProgress));
        }
    }

    public void notifyZoneEnd(List<Integer> nbtIdList) {
        for (int zoneId : nbtIdList) {
            addNbtMessage(zoneId, null);
        }
    }

    @Override
    protected Function<Integer, ZoneMessage> createMessage() {
        return (nbtId) -> new ZoneMessage(new CompoundTag(), currentTime);
    }
}
