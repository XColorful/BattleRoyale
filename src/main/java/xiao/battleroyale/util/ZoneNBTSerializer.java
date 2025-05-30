package xiao.battleroyale.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.api.game.zone.gamezone.GameTag;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;

public class ZoneNBTSerializer {

    public static CompoundTag serializeZoneToNBT(int zoneId, String zoneName, String zoneColor,
                                                 ITickableZone tickableZone, ISpatialZone spatialZone,
                                                 double progress) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(GameTag.ZONE_ID, zoneId);
        tag.putString(GameTag.ZONE_NAME, zoneName);
        tag.putString(GameTag.ZONE_COLOR, zoneColor);

        tag.putString(GameTag.FUNC, tickableZone.getFuncType().getName());

        tag.putString(GameTag.SHAPE, spatialZone.getShapeType().getName());

        CompoundTag centerTag = new CompoundTag();
        Vec3 center = spatialZone.getCenterPos(progress);
        centerTag.putDouble("x", center.x);
        centerTag.putDouble("y", center.y);
        centerTag.putDouble("z", center.z);
        tag.put(GameTag.CENTER, centerTag);

        CompoundTag dimTag = new CompoundTag();
        Vec3 dim = spatialZone.getDimension(progress);
        dimTag.putDouble("x", dim.x);
        dimTag.putDouble("y", dim.y);
        dimTag.putDouble("z", dim.z);
        tag.put(GameTag.DIMENSION, dimTag);

        tag.putDouble(GameTag.PROGRESS, progress);
        return tag;
    }
}