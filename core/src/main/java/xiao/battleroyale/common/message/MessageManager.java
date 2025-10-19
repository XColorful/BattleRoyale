package xiao.battleroyale.common.message;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.network.message.IMessageManager;
import xiao.battleroyale.common.message.game.GameInfoMessageManager;
import xiao.battleroyale.common.message.game.SpectateMessageManager;
import xiao.battleroyale.common.message.team.TeamMessageManager;
import xiao.battleroyale.common.message.zone.ZoneMessageManager;
import xiao.battleroyale.event.handler.message.MessageEventHandler;
import xiao.battleroyale.util.ClassUtils;

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
    private final ClassUtils.ArraySet<IMessageManager> messageManagers = new ClassUtils.ArraySet<>();

    public void tick() {
        currentTime++;
        messageManagers.removeIf(manager -> {
            manager.tickMessage();
            if (manager.messageFinished()) {
                manager.unregister();
                return true;
            }
            return false;
        });
        if (shouldEnd()) {
            MessageEventHandler.unregister();
        }
    }

    private boolean shouldEnd() {
        return messageManagers.isEmpty();
    }

    public boolean isRegistered(IMessageManager messageManager) {
        return messageManagers.contains(messageManager);
    }

    public void registerZoneMessage() {
        ZoneMessageManager manager = ZoneMessageManager.get();
        if (messageManagers.contains(manager)) {
            return;
        }
        manager.register(currentTime);
        messageManagers.add(manager);
        MessageEventHandler.register();
    }
    public void registerTeamMessage() {
        TeamMessageManager manager = TeamMessageManager.get();
        if (messageManagers.contains(manager)) {
            return;
        }
        manager.register(currentTime);
        messageManagers.add(manager);
        MessageEventHandler.register();
    }
    public void registerGameMessage() {
        GameInfoMessageManager manager = GameInfoMessageManager.get();
        if (messageManagers.contains(manager)) {
            return;
        }
        manager.register(currentTime);
        messageManagers.add(manager);
        MessageEventHandler.register();
    }
    public void registerSpectateMessage() {
        SpectateMessageManager manager = SpectateMessageManager.get();
        if (messageManagers.contains(manager)) {
            return;
        }
        manager.register(currentTime);
        messageManagers.add(manager);
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

    public void addGameNbtMessage(int channel, @Nullable CompoundTag nbtMessage) {
        registerGameMessage();
        GameInfoMessageManager.get().notifyNbtChange(channel);
    }
    public void extendGameMessageTime(int channel, int extendTime) {
        GameInfoMessageManager.get().extendMessageTime(channel, extendTime);
    }
    public void notifyGameChange(int channel) {
        GameInfoMessageManager.get().notifyNbtChange(channel);
    }

    public void notifySpectateChange(int singleId) {
        registerSpectateMessage();
        SpectateMessageManager.get().notifyNbtChange(singleId);
    }
}