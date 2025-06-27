package xiao.battleroyale.common.message;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.message.IMessageManager;
import xiao.battleroyale.common.message.team.TeamMessageManager;
import xiao.battleroyale.common.message.zone.ZoneMessageManager;
import xiao.battleroyale.event.MessageEventHandler;

import java.util.*;

public class MessageManager {

    private MessageManager() {}

    private static class MessageManagerHolder {
        private static final MessageManager INSTANCE = new MessageManager();
    }

    public static MessageManager get() {
        return MessageManagerHolder.INSTANCE;
    }

    private int currentTime = 0;
    private final Set<IMessageManager> messageManagers = new HashSet<>(); private final List<IMessageManager> messageManagerList = new ArrayList<>();

    public void tick() {
        currentTime++;
        messageManagerList.removeIf(manager -> {
            manager.tickMessage();
            if (manager.messageFinished()) {
                manager.unregister();
                messageManagers.remove(manager);
                return true;
            }
            return false;
        });
        if (shouldEnd()) {
            MessageEventHandler.unregister();
        }
    }

    private boolean shouldEnd() {
        return messageManagers.isEmpty() && messageManagerList.isEmpty();
    }

    public void registerZoneMessage() {
        ZoneMessageManager manager = ZoneMessageManager.get();
        if (messageManagers.contains(manager)) {
            return;
        }
        manager.register(currentTime);
        messageManagers.add(manager); messageManagerList.add(manager);
        MessageEventHandler.register();
    }

    public void registerTeamMessage() {
        TeamMessageManager manager = TeamMessageManager.get();
        if (messageManagers.contains(manager)) {
            return;
        }
        manager.register(currentTime);
        messageManagers.add(manager); messageManagerList.add(manager);
        MessageEventHandler.register();
    }

    public void addZoneNbtMessage(int zoneId, @Nullable CompoundTag nbtMessage) {
        registerZoneMessage();
        ZoneMessageManager.get().addNbtMessage(zoneId, nbtMessage);
    }
    public void extendZoneMessageTime(int zoneId, int extendTime) {
        ZoneMessageManager.get().extendMessageTime(zoneId, extendTime);
    }
    public void notifyZoneChange(int zoneId) {
        registerZoneMessage();
        ZoneMessageManager.get().notifyNbtChange(zoneId);
    }
    public void notifyZoneEnd(List<Integer> zoneIdList) {
        registerZoneMessage();
        ZoneMessageManager.get().notifyZoneEnd(zoneIdList);
    }

    public void addTeamNbtMessage(int teamId, @Nullable CompoundTag nbtMessage) {
        registerTeamMessage();
        TeamMessageManager.get().addNbtMessage(teamId, nbtMessage);
    }
    public void extendTeamMessageTime(int teamId, int extendTime) {
        TeamMessageManager.get().extendMessageTime(teamId, extendTime);
    }
    public void notifyTeamChange(int teamId) {
        registerTeamMessage();
        TeamMessageManager.get().notifyNbtChange(teamId);
    }
    public void notifyLeavedMember(UUID playerUUID, int teamId) {
        registerTeamMessage();
        TeamMessageManager.get().notifyLeavedMember(playerUUID, teamId);
    }
}
