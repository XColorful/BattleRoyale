package xiao.battleroyale.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.team.TeamTag;
import xiao.battleroyale.api.game.zone.gamezone.GameZoneTag;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.client.game.data.ClientTeamData;

import java.awt.*;
import java.util.List;

public class NBTUtils {

    public static Color parseColorFromString(String colorString) {
        Color color = Color.WHITE;
        try {
            if (colorString.length() == 9 && colorString.startsWith("#")) { // #RRGGBBAA
                int rgba = (int) Long.parseLong(colorString.substring(1), 16);
                int r = (rgba >> 24) & 0xFF;
                int g = (rgba >> 16) & 0xFF;
                int b = (rgba >> 8) & 0xFF;
                int a = rgba & 0xFF;
                color = new Color(r, g, b, a);
            } else { // #RRGGBB
                color = Color.decode(colorString); // 默认 Alpha 为 255
            }
        } catch (NumberFormatException e) {
            BattleRoyale.LOGGER.warn("Failed to decode color hex: {}, reason: {}", colorString, e.getMessage());
        }
        return color;
    }

    public static CompoundTag serializeZoneToNBT(int zoneId, String zoneName, String zoneColor,
                                                 ITickableZone tickableZone, ISpatialZone spatialZone,
                                                 double progress) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(GameZoneTag.ZONE_ID, zoneId);
        tag.putString(GameZoneTag.ZONE_NAME, zoneName);
        tag.putString(GameZoneTag.ZONE_COLOR, zoneColor);

        tag.putString(GameZoneTag.FUNC, tickableZone.getFuncType().getName());

        tag.putString(GameZoneTag.SHAPE, spatialZone.getShapeType().getName());

        CompoundTag centerTag = new CompoundTag();
        Vec3 center = spatialZone.getCenterPos(progress);
        centerTag.putDouble("x", center.x);
        centerTag.putDouble("y", center.y);
        centerTag.putDouble("z", center.z);
        tag.put(GameZoneTag.CENTER, centerTag);

        CompoundTag dimTag = new CompoundTag();
        Vec3 dim = spatialZone.getDimension(progress);
        dimTag.putDouble("x", dim.x);
        dimTag.putDouble("y", dim.y);
        dimTag.putDouble("z", dim.z);
        tag.put(GameZoneTag.DIMENSION, dimTag);

        tag.putDouble(GameZoneTag.PROGRESS, progress);
        return tag;
    }

    public static CompoundTag serializeTeamToNBT(int teamId, String teamColor, List<ClientTeamData.TeamMemberInfo> memberInfos) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(TeamTag.TEAM_ID, teamId);
        tag.putString(TeamTag.TEAM_COLOR, teamColor);

        CompoundTag teamMemberTag = new CompoundTag();
        for (ClientTeamData.TeamMemberInfo memberInfo : memberInfos) {
            CompoundTag memberTag = new CompoundTag();
            memberTag.putString(TeamTag.MEMBER_NAME, memberInfo.name());
            memberTag.putDouble(TeamTag.MEMBER_HEALTH, memberInfo.health());
            memberTag.putInt(TeamTag.MEMBER_BOOST, memberInfo.boost());
            teamMemberTag.put(String.valueOf(memberInfo.playerId()), memberTag);
        }

        tag.put(TeamTag.TEAM_MEMBER, teamMemberTag);
        return tag;
    }
}