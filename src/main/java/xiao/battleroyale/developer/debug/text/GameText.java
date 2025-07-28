package xiao.battleroyale.developer.debug.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.developer.debug.command.sub.get.GetGame;
import xiao.battleroyale.util.ColorUtils;

import java.util.List;

import static xiao.battleroyale.util.CommandUtils.*;

public class GameText {

    /**
     * 悬浮查看GamePlayer各个属性
     */
    public static MutableComponent buildGamePlayerDetail(@Nullable GamePlayer gamePlayer) {
        MutableComponent component = Component.empty();
        if (gamePlayer == null) {
            return component;
        }

        component.append(buildHoverableText("playerUUID", gamePlayer.getPlayerUUID().toString()));
        component.append(Component.literal(" "));
        component.append(buildHoverableText("playerName", gamePlayer.getPlayerName()));
        component.append(Component.literal(" "));
        component.append(buildHoverableText("gameSingleId", String.valueOf(gamePlayer.getGameSingleId())));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("gameTeamColor", gamePlayer.getGameTeamColor(), gamePlayer.getGameTeamColor()));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("bot", String.valueOf(gamePlayer.isBot()), gamePlayer.isBot() ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("isAlive", String.valueOf(gamePlayer.isAlive()), gamePlayer.isAlive() ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("isEliminated", String.valueOf(gamePlayer.isEliminated()), gamePlayer.isEliminated() ? ChatFormatting.RED : ChatFormatting.GRAY));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("isActiveEntity", String.valueOf(gamePlayer.isActiveEntity()), gamePlayer.isActiveEntity() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        component.append(Component.literal(" "));
        component.append(buildHoverableText("invalidTime", String.valueOf(gamePlayer.getInvalidTime())));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("isLeader", String.valueOf(gamePlayer.isLeader()), gamePlayer.isLeader() ? ChatFormatting.AQUA : ChatFormatting.GRAY));
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("lastHealth", String.valueOf(gamePlayer.getLastHealth()), gamePlayer.getLastHealth() > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        component.append(Component.literal(" "));
        component.append(buildHoverableText("lastPos", gamePlayer.getLastPos().toString()));

        return component;
    }
    /**
     * [teamId][singleId]GamePlayer.playerName
     * teamId和[singleId]附上颜色，点击执行命令
     */
    public static MutableComponent buildGamePlayerSimple(@Nullable GamePlayer gamePlayer) {
        MutableComponent component = Component.empty();
        if (gamePlayer == null) {
            return component;
        }

        int teamId = gamePlayer.getGameTeamId();
        int singleId = gamePlayer.getGameSingleId();
        String teamColor = gamePlayer.getGameTeamColor();
        int colorRGB = ColorUtils.parseColorToInt(teamColor) & 0xFFFFFF;

        TextColor minecraftTextColor = TextColor.fromRgb(colorRGB);

        String gameTeamCommand = GetGame.getGameTeamCommandString(teamId);
        String gamePlayerCommand = GetGame.getGamePlayerCommandString(singleId);

        // [teamId]
        MutableComponent teamIdPrefix = Component.literal("[");
        MutableComponent teamIdContent = Component.literal(String.valueOf(teamId));
        teamIdContent.setStyle(Style.EMPTY
                .withColor(minecraftTextColor)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, gameTeamCommand))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(gameTeamCommand))));
        MutableComponent teamIdSuffix = Component.literal("]");

        component.append(teamIdPrefix);
        component.append(teamIdContent);
        component.append(teamIdSuffix);

        // [singleId]
        MutableComponent singleIdComponent = Component.literal("[" + singleId + "]");
        singleIdComponent.setStyle(Style.EMPTY
                .withColor(minecraftTextColor)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, gamePlayerCommand))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(gamePlayerCommand))));
        component.append(singleIdComponent);

        // 添加玩家名称
        if (gamePlayer.isLeader()) {
            component.append(Component.literal(gamePlayer.getPlayerName()).withStyle(ChatFormatting.AQUA));
        } else {
            component.append(Component.literal(gamePlayer.getPlayerName()));
        }

        return component;
    }
    /**
     * 传入GamePlayers列表，返回 [teamId][singleId]Name [teamId][singleId]Name ...
     * 每个前面都加上空格
     */
    public static MutableComponent buildGamePlayersSimple(@NotNull List<GamePlayer> gamePlayers) {
        MutableComponent fullComponent = Component.empty();
        for (GamePlayer gamePlayer : gamePlayers) {
            fullComponent.append(Component.literal(" "));
            fullComponent.append(buildGamePlayerSimple(gamePlayer));
        }
        return fullComponent;
    }

    /**
     * 悬浮查看GameTeam各个属性
     */
    public static MutableComponent buildGameTeamDetail(@Nullable GameTeam gameTeam) {
        MutableComponent component = Component.empty();
        if (gameTeam == null) {
            return component;
        }

        // [gameTeamId]
        component.append(Component.literal(" "));
        component.append(buildHoverableText("gameTeamId", String.valueOf(gameTeam.getGameTeamId())));

        // [gameTeamColor]
        component.append(Component.literal(" "));
        component.append(buildHoverableTextWithColor("gameTeamColor", gameTeam.getGameTeamColor(), gameTeam.getGameTeamColor()));

        // teamMembers (悬浮buildGamePlayersSimple)
        component.append(Component.literal(" "));
        MutableComponent teamMembersHoverContent = buildGamePlayersSimple(gameTeam.getTeamMembers());
        component.append(buildHoverableText("teamMembers", teamMembersHoverContent));

        // leaderUUID
        component.append(Component.literal(" "));
        component.append(buildHoverableText("leaderUUID", String.valueOf(gameTeam.getLeaderUUID())));

        return component;
    }
    /**
     * [teamId][singleId]LeaderName
     * teamId和[singleId]附上颜色，点击执行命令
     */
    public static MutableComponent buildGameTeamSimple(@Nullable GameTeam gameTeam) {
        MutableComponent component = Component.empty();
        if (gameTeam == null) {
            return component;
        }

        int teamId = gameTeam.getGameTeamId();
        String teamColor = gameTeam.getGameTeamColor();
        int colorRGB = ColorUtils.parseColorToInt(teamColor) & 0xFFFFFF;

        TextColor minecraftTextColor = TextColor.fromRgb(colorRGB);

        String gameTeamCommand = GetGame.getGameTeamCommandString(teamId);

        // [teamId]
        MutableComponent teamIdPrefix = Component.literal("[");
        MutableComponent teamIdContent = Component.literal(String.valueOf(teamId));
        teamIdContent.setStyle(Style.EMPTY
                .withColor(minecraftTextColor)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, gameTeamCommand))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(gameTeamCommand))));
        MutableComponent teamIdSuffix = Component.literal("]");

        component.append(teamIdPrefix);
        component.append(teamIdContent);
        component.append(teamIdSuffix);

        // [singleId]LeaderName
        GamePlayer leader = gameTeam.getLeader();
        String gamePlayerCommand = GetGame.getGamePlayerCommandString(leader.getGameSingleId());
        MutableComponent singleIdComponent = Component.literal("[" + leader.getGameSingleId() + "]");
        singleIdComponent.setStyle(Style.EMPTY
                .withColor(minecraftTextColor)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, gamePlayerCommand))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(gamePlayerCommand))));
        component.append(singleIdComponent);

        component.append(Component.literal(leader.getPlayerName()).withStyle(ChatFormatting.AQUA));

        return component;
    }
    /**
     * 传入GameTeams列表，返回 [teamId][singleId]LeaderName [teamId][singleId]LeaderName ...
     * 每个前面都加上空格
     */
    public static MutableComponent buildGameTeamsSimple(@NotNull List<GameTeam> gameTeams) {
        MutableComponent fullComponent = Component.empty();
        for (GameTeam gameTeam : gameTeams) {
            fullComponent.append(Component.literal(" "));
            fullComponent.append(buildGameTeamSimple(gameTeam));
        }
        return fullComponent;
    }

    /**
     * 显示文本
     * zoneId zoneName funcType shapeType zoneColor IGameZone ITickableZone ISpatialZone
     * 悬浮文本
     * IGameZone: preZoneDelayId:int, zoneDelay:int, isCreated:bool, isPresend:bool, isFinished:bool
     * ITickableZone: isReady:bool, tickFrequency:int, tickOffset:int, shapeProgress:double, shapeMoveDelay:int, shapeMoveTime:int
     * ISpatialZone: isWithinZone:bool, isDetermined:bool, startCenter:Vec3/null, startDimension:Vec3/null, startRotate:double, endCenter:Vec3/null, endDimension:Vec3/null, endRotate:double, hasBadShape:bool, segments:int
     * 中间每项加空格
     */
    public static MutableComponent buildGameZoneDetail(@Nullable IGameZone gameZone, Vec3 testPos) {
        MutableComponent component = Component.empty();
        if (gameZone == null) {
            return component;
        }

        // zoneId
        component.append(Component.literal(String.valueOf(gameZone.getZoneId())));
        component.append(Component.literal(" "));

        // zoneName
        component.append(Component.literal(gameZone.getZoneName()));
        component.append(Component.literal(" "));

        // funcType
        component.append(Component.literal(gameZone.getFuncType().getName()));
        component.append(Component.literal(" "));

        // shapeType
        component.append(Component.literal(gameZone.getShapeType().getName()));
        component.append(Component.literal(" "));

        // zoneColor
        component.append(buildHoverableTextWithColor("zoneColor", gameZone.getZoneColor(), gameZone.getZoneColor()));
        component.append(Component.literal(" "));

        // IGameZone 悬浮
        int gameTime = GameManager.get().getGameTime();
        int zoneDelay = gameZone.getZoneDelay();
        double shapeProgress = gameZone.getShapeProgress(gameTime, zoneDelay);
        MutableComponent iGameZoneHover = Component.empty()
                .append(Component.literal("preZoneDelayId:").append(Component.literal(String.valueOf(gameZone.previousZoneDelayId()))))
                .append(Component.literal("\n"))
                .append(Component.literal("zoneDelay:").append(Component.literal(String.valueOf(zoneDelay))))
                .append(Component.literal("\n"))
                .append(Component.literal("isCreated").withStyle(gameZone.isCreated() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + gameZone.isCreated()))
                .append(Component.literal("\n"))
                .append(Component.literal("isPresent").withStyle(gameZone.isPresent() ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + gameZone.isPresent()))
                .append(Component.literal("\n"))
                .append(Component.literal("isFinished").withStyle(gameZone.isFinished() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + gameZone.isFinished()));
        component.append(buildHoverableTextWithColor("IGameZone", iGameZoneHover, gameZone.isCreated() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        component.append(Component.literal(" "));

        // ITickableZone 悬浮
        MutableComponent iTickableZoneHover = Component.empty()
                .append(Component.literal("isReady").withStyle(gameZone.isReady() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + gameZone.isReady()))
                .append(Component.literal("\n"))
                .append(Component.literal("tickFrequency:").append(Component.literal(String.valueOf(gameZone.getTickFrequency()))))
                .append(Component.literal("\n"))
                .append(Component.literal("tickOffset:").append(Component.literal(String.valueOf(gameZone.getTickOffset()))))
                .append(Component.literal("\n"))
                .append(Component.literal("shapeProgress:").append(Component.literal(String.valueOf(shapeProgress))))
                .append(Component.literal("\n"))
                .append(Component.literal("shapeMoveDelay:").append(Component.literal(String.valueOf(gameZone.getShapeMoveDelay()))))
                .append(Component.literal("\n"))
                .append(Component.literal("shapeMoveTime:").append(Component.literal(String.valueOf(gameZone.getShapeMoveTime()))));
        component.append(buildHoverableTextWithColor("ITickableZone", iTickableZoneHover, gameZone.isReady() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        component.append(Component.literal(" "));

        // ISpatialZone 悬浮
        boolean isWithinZone = gameZone.isWithinZone(testPos, shapeProgress);
        Vec3 startCenter = gameZone.getStartCenterPos();
        Vec3 startDim = gameZone.getStartDimension();
        double startRotate = gameZone.getStartRotateDegree();
        Vec3 endCenter = gameZone.getEndCenterPos();
        Vec3 endDim = gameZone.getEndDimension();
        double endRotate = gameZone.getEndRotateDegree();
        MutableComponent iSpatialZoneHover = Component.empty()
                .append(Component.literal("isWithinZone").withStyle(isWithinZone ? ChatFormatting.GREEN : ChatFormatting.RED))
                .append(Component.literal(":" + testPos))
                .append(Component.literal(String.valueOf(isWithinZone)))
                .append(Component.literal("\n"))
                .append(Component.literal("isDetermined").withStyle(gameZone.isDetermined() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + gameZone.isDetermined()))
                .append(Component.literal("\n"))
                .append(Component.literal("startCenter").withStyle(startCenter != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + startCenter))
                .append(Component.literal("\n"))
                .append(Component.literal("startDimension").withStyle(startDim != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + startDim))
                .append(Component.literal("\n"))
                .append(Component.literal("startRotate").withStyle(startRotate != 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + gameZone.getStartRotateDegree()))
                .append(Component.literal("\n"))
                .append(Component.literal("endCenter").withStyle(endCenter != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + endCenter))
                .append(Component.literal("\n"))
                .append(Component.literal("endDimension").withStyle(endDim != null ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + endDim))
                .append(Component.literal("\n"))
                .append(Component.literal("endRotate").withStyle(endRotate != 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY))
                .append(Component.literal(":" + endRotate))
                .append(Component.literal("\n"))
                .append(Component.literal("hasBadShape").withStyle(gameZone.hasBadShape() ? ChatFormatting.YELLOW : ChatFormatting.GRAY))
                .append(Component.literal(":" + gameZone.hasBadShape()))
                .append(Component.literal("\n"))
                .append(Component.literal("segments:").append(Component.literal(String.valueOf(gameZone.getSegments()))));
        component.append(buildHoverableTextWithColor("ISpatialZone", iSpatialZoneHover, gameZone.isDetermined() ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));

        return component;
    }
    /**
     * [zoneId]zoneName
     * []附带颜色
     */
    public static MutableComponent buildGameZoneSimple(@Nullable IGameZone gameZone) {
        MutableComponent component = Component.empty();
        if (gameZone == null) {
            return component;
        }

        int zoneId = gameZone.getZoneId();
        String zoneName = gameZone.getZoneName();
        int colorRGB = ColorUtils.parseColorToInt(gameZone.getZoneColor()) & 0xFFFFFF;
        TextColor zoneColor = TextColor.fromRgb(colorRGB);

        String zoneCommand = GetGame.getGameZoneCommandString(zoneId);

        component.append(Component.literal("[").withStyle(Style.EMPTY.withColor(zoneColor)))
                .append(Component.literal(String.valueOf(zoneId))
                                .setStyle(Style.EMPTY
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, zoneCommand))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(zoneCommand)))
                                ))
                .append(Component.literal("]").withStyle(Style.EMPTY.withColor(zoneColor)))
                .append(Component.literal(zoneName));

        return component;
    }

    public static MutableComponent buildGameZonesSimple(@NotNull List<IGameZone> gameZones) {
        MutableComponent fullComponent = Component.empty();
        for (IGameZone gameZone : gameZones) {
            fullComponent.append(Component.literal(" "));
            fullComponent.append(buildGameZoneSimple(gameZone));
        }
        return fullComponent;
    }
}
