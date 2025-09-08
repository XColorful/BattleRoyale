package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.message.zone.GameZoneTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientGameData;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.client.game.data.TeamMemberInfo;
import xiao.battleroyale.common.message.game.GameMessageManager;
import xiao.battleroyale.common.message.game.SpectateMessage;
import xiao.battleroyale.common.message.game.SpectateMessageManager;
import xiao.battleroyale.common.message.team.TeamMessageManager;
import xiao.battleroyale.common.message.zone.ZoneMessage;
import xiao.battleroyale.common.message.zone.ZoneMessageManager;
import xiao.battleroyale.developer.debug.text.MessageText;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DebugMessage {

    private static class DebugMessageHolder {
        private static final DebugMessage INSTANCE = new DebugMessage();
    }

    public static DebugMessage get() {
        return DebugMessageHolder.INSTANCE;
    }

    private DebugMessage() {
        ;
    }

    /**
     * [调试]getMessages:
     */
    public static final String GET_MESSAGES = "getMessages";
    public void getMessages(CommandSourceStack source) {
        DebugManager.sendDebugMessage(source, GET_MESSAGES, MessageText.buildMessagesSimple());
    }
    public void getMessagesLocal(CommandSourceStack source) {
        DebugManager.sendDebugMessage(source, GET_MESSAGES, MessageText.buildMessagesSimpleLocal(ClientGameDataManager.get()));
    }

    /**
     * [调试]getZoneMessages:
     */
    public static final String GET_ZONE_MESSAGES = "getZoneMessages";
    public void getZoneMessages(CommandSourceStack source, int min, int max) {
        List<Integer> idList = ZoneMessageManager.get().getMessagesIdList().stream()
                .filter(id -> id >= min && id <= max)
                .sorted()
                .toList();
        DebugManager.sendDebugMessage(source, GET_ZONE_MESSAGES, MessageText.buildZoneMessagesDetail(ZoneMessageManager.get(), idList));
    }
    public void getZoneMessagesLocal(CommandSourceStack source, int min, int max) {
        List<ClientSingleZoneData> zoneData = ClientGameDataManager.get().getActiveZones().entrySet().stream()
                .filter(entry -> entry.getKey() >= min && entry.getKey() <= max)
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparingInt(data -> data.id))
                .toList();
        DebugManager.sendLocalDebugMessage(source, GET_ZONE_MESSAGES, MessageText.buildZoneMessagesDetailLocal(zoneData));
    }
    public static final String GET_TEAM_MESSAGES = "getTeamMessages";
    public void getTeamMessages(CommandSourceStack source, int min, int max) {
        List<Integer> idList = TeamMessageManager.get().getMessagesIdList().stream()
                .filter(id -> id >= min && id <= max)
                .sorted()
                .toList();
        DebugManager.sendDebugMessage(source, GET_TEAM_MESSAGES, MessageText.buildTeamMessagesDetail(TeamMessageManager.get(), idList));
    }
    public void getTeamMessagesLocal(CommandSourceStack source, int min, int max) {
        ClientTeamData teamData = ClientGameDataManager.get().getTeamData();
        List<TeamMemberInfo> teamMemberInfoList = teamData.teamMemberInfoList.stream()
                .filter(data -> data.playerId >= min && data.playerId <= max)
                .toList();
        DebugManager.sendLocalDebugMessage(source, GET_TEAM_MESSAGES, MessageText.buildTeamMessagesDetailLocal(teamData, teamMemberInfoList));
    }
    public static final String GET_GAME_MESSAGES = "getGameMessages";
    public void getGameMessages(CommandSourceStack source, int min, int max) {
        List<Integer> idList = GameMessageManager.get().getMessagesIdList().stream()
                .filter(id -> id >= min && id <= max)
                .sorted()
                .toList();
        DebugManager.sendDebugMessage(source, GET_GAME_MESSAGES, MessageText.buildGameMessagesDetail(GameMessageManager.get(), idList));
    }
    public void getGameMessagesLocal(CommandSourceStack source, int min, int max) {
        CompoundTag messageNbt = ClientGameDataManager.get().getGameData().lastMessageNbt;
        List<String> keyList = messageNbt.getAllKeys().stream()
                .filter(keyString -> {
                    try {
                        int keyInt = Integer.parseInt(keyString);
                        return keyInt >= min && keyInt <= max;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .sorted()
                .toList();
        DebugManager.sendLocalDebugMessage(source, GET_GAME_MESSAGES, MessageText.buildGameMessagesDetailLocal(keyList));
    }
    public static final String GET_SPECTATE_MESSAGES = "getSpectateMessages";
    public void getSpectateMessages(CommandSourceStack source, int min, int max) {
        List<Integer> idList = SpectateMessageManager.get().getMessagesIdList().stream()
                .filter(id -> id >= min && id <= max)
                .sorted()
                .toList();
        DebugManager.sendDebugMessage(source, GET_SPECTATE_MESSAGES, MessageText.buildSpectateMessagesDetail(SpectateMessageManager.get(), idList));
    }
    public void getSpectateMessagesLocal(CommandSourceStack source, int min, int max) {
        CompoundTag messageNbt = ClientGameDataManager.get().getGameData().getSpectateData().lastMessageNbt;
        List<String> keyList = messageNbt.getAllKeys().stream()
                .filter(keyString -> {
                    try {
                        int keyInt = Integer.parseInt(keyString);
                        return keyInt >= min && keyInt <= max;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .sorted()
                .toList();
        DebugManager.sendLocalDebugMessage(source, GET_SPECTATE_MESSAGES, MessageText.buildSpectateMessagesDetailLocal(keyList));
    }

    /**
     * [调试]getZoneMessage:
     */
    public static final String GET_ZONE_MESSAGE = "getZoneMessage";
    public void getZoneMessage(CommandSourceStack source, int nbtId) {
        DebugManager.sendDebugMessage(source, GET_ZONE_MESSAGE, MessageText.buildZoneMessageDetail(ZoneMessageManager.get().getMessage(nbtId), nbtId));
    }
    public void getZoneMessageLocal(CommandSourceStack source, int nbtId) {
        ClientSingleZoneData zoneData = ClientGameDataManager.get().getActiveZones().get(nbtId);
        CompoundTag lastMessageNbt = zoneData != null ? zoneData.lastMessageNbt : null;
        int lastUpdateTick = zoneData != null ? (int) zoneData.getLastUpdateTick() : Integer.MIN_VALUE;
        DebugManager.sendLocalDebugMessage(source, GET_ZONE_MESSAGE, MessageText.buildZoneMessageDetailLocal(lastMessageNbt, nbtId, lastUpdateTick));
    }
    public void getZoneMessage(CommandSourceStack source, String name) {
        ZoneMessageManager zoneMessageManager = ZoneMessageManager.get();
        List<Integer> idList = zoneMessageManager.getMessagesIdList();
        int targetId = Integer.MIN_VALUE;
        for (int nbtId : idList) {
            ZoneMessage zoneMessage = zoneMessageManager.getMessage(nbtId);
            if (zoneMessage == null) {
                continue;
            }
            if (zoneMessage.nbt.getString(GameZoneTag.ZONE_NAME).equals(name)) {
                targetId = nbtId;
                break;
            }
        }
        getZoneMessage(source, targetId);
    }
    public void getZoneMessageLocal(CommandSourceStack source, String name) {
        Map<Integer, ClientSingleZoneData> clientZoneData = ClientGameDataManager.get().getActiveZones();
        int targetId = Integer.MIN_VALUE;
        for (Map.Entry<Integer, ClientSingleZoneData> entry : clientZoneData.entrySet()) {
            if (entry.getValue().name.equals(name)) {
                targetId = entry.getKey();
                break;
            }
        }
        getZoneMessageLocal(source, targetId);
    }
    public static final String GET_TEAM_MESSAGE = "getTeamMessage";
    public void getTeamMessage(CommandSourceStack source, int nbtId) {
        DebugManager.sendDebugMessage(source, GET_TEAM_MESSAGE, MessageText.buildTeamMessageDetail(TeamMessageManager.get().getMessage(nbtId), nbtId));
    }
    public void getTeamMessageLocal(CommandSourceStack source, int playerId) {
        ClientTeamData teamData = ClientGameDataManager.get().getTeamData();
        TeamMemberInfo teamMemberInfo = null;
        for (TeamMemberInfo memberInfo : teamData.teamMemberInfoList) {
            if (memberInfo.playerId == playerId) {
                teamMemberInfo = memberInfo;
                break;
            }
        }
        DebugManager.sendLocalDebugMessage(source, GET_TEAM_MESSAGE, MessageText.buildTeamMessageDetailLocal(teamMemberInfo, (int) teamData.getLastUpdateTick()));
    }
    public static final String GET_GAME_MESSAGE = "getGameMessage";
    public void getGameMessage(CommandSourceStack source, int nbtId) {
        DebugManager.sendDebugMessage(source, GET_GAME_MESSAGE, MessageText.buildGameMessageDetail(GameMessageManager.get().getMessage(nbtId), nbtId));
    }
    public void getGameMessageLocal(CommandSourceStack source, int nbtId) {
        ClientGameData gameData = ClientGameDataManager.get().getGameData();
        CompoundTag messageNbt = gameData.lastMessageNbt;
        CompoundTag nbt = null;
        String nbtIdString = Integer.toString(nbtId);
        for (String key : messageNbt.getAllKeys()) {
            if (key.equals(nbtIdString)) {
                nbt = messageNbt.getCompound(key);
            }
        }
        DebugManager.sendLocalDebugMessage(source, GET_GAME_MESSAGE, MessageText.buildGameMessageDetailLocal(nbt, (int) gameData.getLastUpdateTick()));
    }
    public static final String GET_SPECTATE_MESSAGE = "getSpectateMessage";
    public void getSpectateMessage(CommandSourceStack source, int nbtId) {
        DebugManager.sendDebugMessage(source, GET_SPECTATE_MESSAGE, MessageText.buildSpectateMessageDetail(SpectateMessageManager.get().getMessage(nbtId), nbtId));
    }
    public void getSpectateMessageLocal(CommandSourceStack source, int nbtId) {
        ClientGameData.ClientSpectateData spectateData = ClientGameDataManager.get().getGameData().getSpectateData();
        CompoundTag messageNbt = spectateData.lastMessageNbt;
        CompoundTag nbt = null;
        String nbtIdString = Integer.toString(nbtId);
        for (String key : messageNbt.getAllKeys()) {
            if (key.equals(nbtIdString)) {
                nbt = messageNbt.getCompound(key);
            }
        }
        DebugManager.sendLocalDebugMessage(source, GET_SPECTATE_MESSAGE, MessageText.buildSpectateMessageDetailLocal(nbt, (int) spectateData.getLastUpdateTick()));
    }
}
