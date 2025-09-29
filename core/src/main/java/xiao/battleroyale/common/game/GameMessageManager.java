package xiao.battleroyale.common.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.common.message.game.GameInfoMessageManager;

import java.util.List;
import java.util.UUID;

public class GameMessageManager {

    private static final MessageManager messageManagerInstance = MessageManager.get();

    public static void addZoneNbtMessage(int zoneId, @Nullable CompoundTag nbtMessage) {
        messageManagerInstance.addZoneNbtMessage(zoneId, nbtMessage);
    }
    public static void notifyZoneEnd(List<Integer> zoneIdList) {
        messageManagerInstance.notifyZoneEnd(zoneIdList);
    }
    public static void notifyTeamChange(int teamId) {
        messageManagerInstance.notifyTeamChange(teamId);
    }
    public static void notifyLeavedMember(UUID playerUUID, int teamId) {
        messageManagerInstance.notifyLeavedMember(playerUUID, teamId);
    }
    public static void notifyAliveChange() {
        messageManagerInstance.notifyGameChange(GameInfoMessageManager.ALIVE_CHANNEL);
    }

}
