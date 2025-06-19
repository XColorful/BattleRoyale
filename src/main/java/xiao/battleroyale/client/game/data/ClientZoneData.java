package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.GameZoneTag;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.ColorUtils;
import xiao.battleroyale.util.StringUtils;

import java.awt.*;

public class ClientZoneData {

    public final int id;
    public String name;
    public Color color;

    public ZoneFuncType funcType;

    public ZoneShapeType shapeType;
    public Vec3 center;
    public Vec3 dimension;
    public double rotateDegree = 0;
    public int segments = 3; // 供多边形使用
    public float angle = (float) (Math.PI / 2.0); // 使正上方为一个顶点

    public double progress; // [0, 1]

    public ClientZoneData(int id) {
        this.id = id;
    }

    public void updateFromNbt(CompoundTag nbt) {
        this.name = nbt.getString(GameZoneTag.ZONE_NAME);
        this.color = ColorUtils.parseColorFromString(nbt.getString(GameZoneTag.ZONE_COLOR));

        String funcTypeName = nbt.getString(GameZoneTag.FUNC);
        this.funcType = ZoneFuncType.fromName(funcTypeName);
        if (this.funcType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneFuncType: {}", funcTypeName);
        }

        String shapeTypeName = nbt.getString(GameZoneTag.SHAPE);
        this.shapeType = ZoneShapeType.fromName(shapeTypeName);
        if (this.shapeType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneShapeType: {}", shapeTypeName);
        } else if (this.shapeType == ZoneShapeType.POLYGON) {
            this.segments = nbt.getInt(GameZoneTag.SEGMENTS);
        }

        CompoundTag centerTag = nbt.getCompound(GameZoneTag.CENTER);
        this.center = new Vec3(centerTag.getDouble("x"), centerTag.getDouble("y"), centerTag.getDouble("z"));

        CompoundTag dimTag = nbt.getCompound(GameZoneTag.DIMENSION);
        this.dimension = new Vec3(dimTag.getDouble("x"), dimTag.getDouble("y"), dimTag.getDouble("z"));

        this.rotateDegree = nbt.contains(GameZoneTag.ROTATE) ? nbt.getDouble(GameZoneTag.ROTATE) : 0;

        this.progress = nbt.getDouble(GameZoneTag.PROGRESS);
    }
}
