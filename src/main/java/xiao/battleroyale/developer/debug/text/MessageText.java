package xiao.battleroyale.developer.debug.text;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.message.team.GameTeamTag;
import xiao.battleroyale.api.message.zone.GameZoneTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.client.game.data.TeamMemberInfo;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.common.message.game.GameInfoMessage;
import xiao.battleroyale.common.message.game.GameInfoMessageManager;
import xiao.battleroyale.common.message.game.SpectateMessage;
import xiao.battleroyale.common.message.game.SpectateMessageManager;
import xiao.battleroyale.common.message.team.TeamMessage;
import xiao.battleroyale.common.message.team.TeamMessageManager;
import xiao.battleroyale.common.message.zone.ZoneMessage;
import xiao.battleroyale.common.message.zone.ZoneMessageManager;
import xiao.battleroyale.developer.debug.command.sub.get.GetGame;
import xiao.battleroyale.developer.debug.command.sub.get.GetMessage;
import xiao.battleroyale.util.ColorUtils;

import java.util.List;

import static xiao.battleroyale.util.CommandUtils.*;

public class MessageText {

    /**
     * 各个MessageManager消息数量
     * 点击展开消息ID列表
     */
    public static MutableComponent buildMessagesSimple() {
        MutableComponent component = Component.empty();

        component.append(buildZoneMessagesSimple(ZoneMessageManager.get()))
                .append(Component.literal(" "))
                .append(buildTeamMessagesSimple(TeamMessageManager.get()))
                .append(Component.literal(" "))
                .append(buildGameMessagesSimple(GameInfoMessageManager.get()))
                .append(Component.literal(" "))
                .append(buildSpectateMessagesSimple(SpectateMessageManager.get()));

        return component;
    }
    public static MutableComponent buildMessagesSimpleLocal(ClientGameDataManager clientGameDataManager) {
        MutableComponent component = Component.empty();

        component.append(buildZoneMessagesSimpleLocal(clientGameDataManager))
                .append(Component.literal(" "))
                .append(buildTeamMessagesSimpleLocal(clientGameDataManager))
                .append(Component.literal(" "))
                .append(buildGameMessagesSimpleLocal(clientGameDataManager))
                .append(Component.literal(" "))
                .append(buildSpectateMessagesSimpleLocal(clientGameDataManager));

        return component;
    }

    /**
     * [总消息数]ZoneMessages
     * 点击ZoneMessages展开ZoneMessagesDetail
     */
    public static MutableComponent buildZoneMessagesSimple(ZoneMessageManager zoneMessageManager) {
        return buildMessagesCommonSimple(zoneMessageManager.messagesSize(), GetMessage.getZoneMessagesCommand(0, 10), "ZoneMessages");
    }
    public static MutableComponent buildTeamMessagesSimple(TeamMessageManager teamMessageManager) {
        return buildMessagesCommonSimple(teamMessageManager.messagesSize(), GetMessage.getTeamMessagesCommand(1, 10), "TeamMessages");
    }
    public static MutableComponent buildGameMessagesSimple(GameInfoMessageManager gameInfoMessageManager) {
        return buildMessagesCommonSimple(gameInfoMessageManager.messagesSize(), GetMessage.getGameMessagesCommand(-10, 0), "GameMessages");
    }
    public static MutableComponent buildSpectateMessagesSimple(SpectateMessageManager spectateMessageManager) {
        return buildMessagesCommonSimple(spectateMessageManager.messagesSize(), GetMessage.getSpectateMessagesCommand(1, 20), "SpectateMessages");
    }
    public static MutableComponent buildMessagesCommonSimple(int messageSize, String command, String displayName) {
        return Component.empty()
                .append(buildRunnableIntBracketWithColor(messageSize, command, messageSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(displayName).withStyle(messageSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
    }
    public static MutableComponent buildZoneMessagesSimpleLocal(ClientGameDataManager clientGameDataManager) {
        int size = clientGameDataManager.getActiveZones().size();
        String command = GetMessage.getLocalZoneMessagesCommand(0, 10);
        return buildMessagesCommonSimpleLocal(size, command, "ZoneMessages");
    }
    public static MutableComponent buildTeamMessagesSimpleLocal(ClientGameDataManager clientGameDataManager) {
        int size = clientGameDataManager.getTeamData().teamMemberInfoList.size(); // 单队信息
        String command = GetMessage.getLocalTeamMessagesCommand(0, 100); // 队内玩家singleId范围
        return buildMessagesCommonSimpleLocal(size, command, "TeamMembers");
    }
    public static MutableComponent buildGameMessagesSimpleLocal(ClientGameDataManager clientGameDataManager) {
        int size = clientGameDataManager.getGameData().lastMessageNbt.getAllKeys().size();
        String command = GetMessage.getLocalGameMessagesCommand(-10, 0);
        return buildMessagesCommonSimpleLocal(size, command, "GameMessages");
    }
    public static MutableComponent buildSpectateMessagesSimpleLocal(ClientGameDataManager clientGameDataManager) {
        int size = clientGameDataManager.getGameData().getSpectateData().lastMessageNbt.getAllKeys().size();
        String command = GetMessage.getLocalSpectateMessagesCommand(-10, 0);
        return buildMessagesCommonSimpleLocal(size, command, "SpectateMessages");
    }
    public static MutableComponent buildMessagesCommonSimpleLocal(int messageSize, String command, String displayName) {
        return Component.empty()
                .append(buildSuggestableIntBracketWithColor(messageSize, command, messageSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(displayName).withStyle(messageSize > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
    }

    /**
     * 查看单个消息id, NBT，updateTime
     */
    public static MutableComponent buildZoneMessageDetail(ZoneMessage zoneMessage, int displayId) {
        MutableComponent component = Component.empty();
        if (zoneMessage == null) {
            return component;
        }
        return component.append(buildZoneMessageSimple(zoneMessage, displayId))
                .append(Component.literal(" "))
                .append(buildMessageCommonDetail(zoneMessage.nbt, zoneMessage.updateTime));
    }
    public static MutableComponent buildTeamMessageDetail(TeamMessage teamMessage, int displayId) {
        MutableComponent component = Component.empty();
        if (teamMessage == null) {
            return component;
        }
        return component.append(buildTeamMessageSimple(teamMessage, displayId))
                .append(Component.literal(" "))
                .append(buildMessageCommonDetail(teamMessage.nbt, teamMessage.updateTime));
    }
    public static MutableComponent buildGameMessageDetail(GameInfoMessage gameInfoMessage, int displayId) {
        MutableComponent component = Component.empty();
        if (gameInfoMessage == null) {
            return component;
        }
        return component.append(buildGameMessageSimple(gameInfoMessage, displayId))
                .append(Component.literal(" "))
                .append(buildMessageCommonDetail(gameInfoMessage.nbt, gameInfoMessage.updateTime));
    }
    public static MutableComponent buildSpectateMessageDetail(SpectateMessage spectateMessage, int displayId) {
        MutableComponent component = Component.empty();
        if (spectateMessage == null) {
            return component;
        }
        return component.append(buildSpectateMessageSimple(spectateMessage, displayId))
                .append(Component.literal(" "))
                .append(buildMessageCommonDetail(spectateMessage.nbt, spectateMessage.updateTime));
    }
    public static MutableComponent buildMessageCommonDetail(CompoundTag nbt, int updateTime) {
        MutableComponent component = Component.empty();
        if (nbt == null) {
            return component;
        }
        // nbt
        MutableComponent nbtComponent = buildNbtVerticalList(nbt);
        component.append(buildHoverableTextWithColor("nbt", nbtComponent, !nbt.getAllKeys().isEmpty() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        component.append(Component.literal(" "));
        // updateTime
        component.append(buildHoverableText("updateTime", String.valueOf(updateTime)));

        return component;
    }
    public static MutableComponent buildZoneMessageDetailLocal(CompoundTag messageNbt, int displayId, int lastUpdateTime) {
        MutableComponent component = Component.empty();

        return component.append(buildZoneMessageSimpleLocal(messageNbt, displayId))
                .append(Component.literal(" "))
                .append(buildMessageCommonDetail(messageNbt, lastUpdateTime));
    }
    public static MutableComponent buildTeamMessageDetailLocal(TeamMemberInfo memberInfo, int lastUpdateTime) {
        MutableComponent component = Component.empty();
        if (memberInfo == null) {
            return component;
        }
        return component.append(buildHoverableText("playerId", String.valueOf(memberInfo.playerId)))
                .append(Component.literal(" "))
                .append(buildHoverableText("name", String.valueOf(memberInfo.name)))
                .append(Component.literal(" "))
                .append(buildHoverableTextWithColor("health", String.valueOf(memberInfo.health), memberInfo.health > 0 ? ChatFormatting.GREEN : ChatFormatting.RED))
                .append(Component.literal(" "))
                .append(buildHoverableTextWithColor("boost", String.valueOf(memberInfo.boost), memberInfo.boost > 0 ? ChatFormatting.YELLOW : ChatFormatting.DARK_GRAY))
                .append(Component.literal(" "))
                .append(buildHoverableTextWithColor("uuid", String.valueOf(memberInfo.uuid), ChatFormatting.GRAY))
                .append(Component.literal(" "))
                .append(buildHoverableText("updateTime", String.valueOf(lastUpdateTime)));
    }
    public static MutableComponent buildGameMessageDetailLocal(CompoundTag nbt, int lastUpdateTime) {
        return buildMessageCommonDetail(nbt, lastUpdateTime);
    }
    public static MutableComponent buildSpectateMessageDetailLocal(CompoundTag nbt, int lastUpdateTime) {
        return buildMessageCommonDetail(nbt, lastUpdateTime);
    }

    /**
     * [id]
     * 点击查看单个区域详细消息
     */
    public static MutableComponent buildZoneMessageSimple(ZoneMessage zoneMessage, int displayId) {
        if (zoneMessage == null) {
            return Component.empty();
        }

        String zoneColor = zoneMessage.nbt.getString(GameZoneTag.ZONE_COLOR);
        TextColor textColor = TextColor.fromRgb(ColorUtils.parseColorToInt(zoneColor));

        String messageCommand = GetMessage.getZoneMessageCommand(displayId);

        return buildRunnableIntBracketWithColor(displayId, messageCommand, textColor);
    }
    public static MutableComponent buildZoneMessageSimpleLocal(CompoundTag messageNbt, int displayId) {
        if (messageNbt == null) {
            return Component.empty();
        }

        String zoneColor = messageNbt.getString(GameZoneTag.ZONE_COLOR);
        TextColor textColor = TextColor.fromRgb(ColorUtils.parseColorToInt(zoneColor));

        String messageCommand = GetMessage.getLocalZoneMessageCommand(displayId);

        return buildSuggestableIntBracketWithColor(displayId, messageCommand, textColor);
    }
    // [zoneId]
    public static MutableComponent buildZoneMessageSimpleLocal(ClientSingleZoneData zoneData) {
        TextColor textColor = TextColor.fromRgb(zoneData.color.getRGB());
        String messageComand = GetMessage.getLocalZoneMessageCommand(zoneData.id);
        return buildSuggestableIntBracketWithColor(zoneData.id, messageComand, textColor);
    }

    /**
     * [id]Team
     * 点击查看单个队伍详细消息
     * 点击Team查看GameTeam
     */
    public static MutableComponent buildTeamMessageSimple(TeamMessage teamMessage, int displayId) {
        if (teamMessage == null) {
            return Component.empty();
        }

        String teamColor = teamMessage.nbt.getString(GameTeamTag.TEAM_COLOR);
        TextColor textColor = TextColor.fromRgb(ColorUtils.parseColorToInt(teamColor));

        String messageCommand = GetMessage.getTeamMessageCommand(displayId);
        String gameTeamCommand = GetGame.getGameTeamCommand(displayId);
        GameTeam gameTeam = GameTeamManager.getGameTeamById(displayId);
        String gameTeamColor = gameTeam != null ? gameTeam.getGameTeamColor() : "";
        TextColor gameTeamTextColor = TextColor.fromRgb(ColorUtils.parseColorToInt(gameTeamColor));

        return buildRunnableIntBracketWithColor(displayId, messageCommand, textColor)
                .append(Component.literal("Team").withStyle(Style.EMPTY.withColor(gameTeamTextColor)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, gameTeamCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(gameTeamCommand)))));
    }

    /**
     * [id]
     */
    public static MutableComponent buildGameMessageSimple(GameInfoMessage gameInfoMessage, int displayId) {
        if (gameInfoMessage == null) {
            return Component.empty();
        }

        String messageCommand = GetMessage.getGameMessageCommand(displayId);

        return buildRunnableIntBracket(displayId, messageCommand);
    }

    /**
     * [id]
     */
    public static MutableComponent buildSpectateMessageSimple(SpectateMessage spectateMessage, int displayId) {
        if (spectateMessage == null) {
            return Component.empty();
        }

        String messageCommand = GetMessage.getSpectateMessageCommand(displayId);

        return buildRunnableIntBracket(displayId, messageCommand);
    }

    public static MutableComponent buildMessagesCommonDetail(AbstractMessageManager<?> messageManager) {
        MutableComponent component = Component.empty();

        MutableComponent configComponent = Component.empty();
        configComponent.append(Component.literal("cleanFrequency"))
                .append(Component.literal(":" + messageManager.cleanFrequency()))
                .append(Component.literal("\n"))
                .append(Component.literal("expireTime"))
                .append(Component.literal(":" + messageManager.expireTime()))
                .append(Component.literal("\n"))
                .append(Component.literal("forceSyncFrequency"))
                .append(Component.literal(":" + messageManager.forceSyncFrequency()));
        component.append(buildHoverableText("config", configComponent));
        component.append(Component.literal(" "));

        MutableComponent progressComponent = Component.empty();
        int p2 = messageManager.messagesSize();
        int p3 = messageManager.changedIdSize();
        progressComponent.append(Component.literal("currentTime"))
                .append(Component.literal(":" + messageManager.getCurrentTime()))
                .append(Component.literal("\n"))
                .append(Component.literal("messages").withStyle(p2 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p2))
                .append(Component.literal("\n"))
                .append(Component.literal("changedId").withStyle(p3 > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + p3))
                .append(Component.literal("\n"))
                .append(Component.literal("lastCleanTime"))
                .append(Component.literal(":" + messageManager.getLastCleanTime()));
        component.append(buildHoverableText("progress", progressComponent));

        return component;
    }

    /**
     * config progress [zoneId1] [zoneId2] ...
     * 区域消息ID列表
     * 点击任意ID查看具体消息
     */
    public static MutableComponent buildZoneMessagesDetail(ZoneMessageManager zoneMessageManager, List<Integer> idList) {
        MutableComponent component = Component.empty();

        component.append(buildMessagesCommonDetail(zoneMessageManager));
        for (int id : idList) {
            component.append(Component.literal(" "))
                    .append(buildZoneMessageSimple(zoneMessageManager.getMessage(id), id));
        }

        return component;
    }
    // zoneExpireTick [zoneId] [zoneId] ...
    public static MutableComponent buildZoneMessagesDetailLocal(List<ClientSingleZoneData> zoneData) {
        MutableComponent component = Component.empty();

        component.append(buildHoverableText("zoneExpireTick", String.valueOf(ClientGameDataManager.ZONE_EXPIRE_TICK)));
        for (ClientSingleZoneData data : zoneData) {
            component.append(Component.literal(" "))
                    .append(buildZoneMessageSimpleLocal(data));
        }
        return component;
    }
    /**
     * config progress [teamId1]Team [teamId2]Team ...
     * 队伍消息ID列表
     * 点击任意ID查看具体消息
     * 点击Team查看GameTeam
     */
    public static MutableComponent buildTeamMessagesDetail(TeamMessageManager teamMessageManager, List<Integer> idList) {
        MutableComponent component = Component.empty();

        component.append(buildMessagesCommonDetail(teamMessageManager));
        for (int id : idList) {
            component.append(Component.literal(" "))
                    .append(buildTeamMessageSimple(teamMessageManager.getMessage(id), id));
        }

        return component;
    }
    // teamExpireTick [teamId][singleId][Name] ...
    public static MutableComponent buildTeamMessagesDetailLocal(ClientTeamData clientTeamData, List<TeamMemberInfo> teamMemberInfoList) {
        MutableComponent component = Component.empty();
        // clientTeamData含队伍信息
        // teamMemberInfoList是过滤后的列表

        component.append(buildHoverableText("teamExpireTick", String.valueOf(ClientGameDataManager.TEAM_EXPIRE_TICK)));
        TextColor textColor = TextColor.fromRgb(clientTeamData.teamColor.getRGB());
        for (TeamMemberInfo memberInfo : teamMemberInfoList) {
            // [teamId][singleId]Name
            String messageCommand = GetMessage.getLocalTeamMessageCommand(memberInfo.playerId);
            component.append(Component.literal(" "))
                    .append(buildSuggestableIntBracketWithColor(clientTeamData.teamId, messageCommand, textColor))
                    .append(buildSuggestableIntBracketWithFullColor(memberInfo.playerId, messageCommand, textColor))
                    .append(Component.literal(memberInfo.name).withStyle(memberInfo.health > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        }

        return component;
    }
    /**
     * config progress [id] [id] ...
     * 点击任意ID查看具体消息
     */
    public static MutableComponent buildGameMessagesDetail(GameInfoMessageManager gameInfoMessageManager, List<Integer> idList) {
        MutableComponent component = Component.empty();

        component.append(buildMessagesCommonDetail(gameInfoMessageManager));
        for (int id : idList) {
            component.append(Component.literal(" "))
                    .append(buildGameMessageSimple(gameInfoMessageManager.getMessage(id), id));
        }

        return component;
    }
    public static MutableComponent buildGameMessagesDetailLocal(List<String> keyList) { // 输入keyList已经保证key均能转换整数
        MutableComponent component = Component.empty();

        component.append(buildHoverableText("gameExpireTick", String.valueOf(ClientGameDataManager.GAME_EXPIRE_TICK)));
        for (String key : keyList) {
            int nbtId;
            try {
                nbtId = Integer.parseInt(key);
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Unexpected Integer parse error:{}", key);
                continue;
            }
            String messageCommand = GetMessage.getLocalGameMessageCommand(nbtId);
            component.append(Component.literal(" "))
                    .append(buildSuggestableIntBracket(nbtId, messageCommand));
        }

        return component;
    }

    /**
     * config progress [id] [id] ...
     * 点击任意ID查看具体消息
     */
    public static MutableComponent buildSpectateMessagesDetail(SpectateMessageManager spectateMessageManager, List<Integer> idList) {
        MutableComponent component = Component.empty();

        component.append(buildMessagesCommonDetail(spectateMessageManager));
        for (int id : idList) {
            component.append(Component.literal(" "))
                    .append(buildSpectateMessageSimple(spectateMessageManager.getMessage(id), id));
        }

        return component;
    }
    public static MutableComponent buildSpectateMessagesDetailLocal(List<String> keyList) { // 输入keyList已经保证key均能转换整数
        MutableComponent component = Component.empty();

        component.append(buildHoverableText("gameExpireTick", String.valueOf(ClientGameDataManager.GAME_EXPIRE_TICK)));
        for (String key : keyList) {
            int nbtId;
            try {
                nbtId = Integer.parseInt(key);
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Unexpected Integer parse error:{}", key);
                continue;
            }
            String messageCommand = GetMessage.getLocalSpectateMessageCommand(nbtId);
            component.append(Component.literal(" "))
                    .append(buildSuggestableIntBracket(nbtId, messageCommand));
        }
        return component;
    }
}
