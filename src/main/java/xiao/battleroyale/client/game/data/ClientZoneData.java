package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.GameZoneTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;

public class ClientZoneData {

    public boolean useClientColor = false; // TODO 添加配置
    public String clientColorString = "#0000FF"; // 半透明蓝色

    public final int id;
    public String name;
    public Color color;
    public ZoneFuncType funcType;
    public ZoneShapeType shapeType;
    public Vec3 center;
    public Vec3 dimension;
    public double rotateDegree = 0;
    public int segments = 3; // 供多边形和星形使用
    public double progress; // [0, 1]

    public long lastUpdateTime = 0;

    public ClientZoneData(int id) {
        this.id = id;
    }

    /*
    * 推迟到主线程
     */
    public void updateFromNbt(CompoundTag nbt) {
        this.name = nbt.getString(GameZoneTag.ZONE_NAME);
        this.color = ColorUtils.parseColorFromString(nbt.getString(GameZoneTag.ZONE_COLOR));
        if (useClientColor) {
            this.color = ColorUtils.changeColorExceptAlpha(this.color, clientColorString);
        }

        String funcTypeName = nbt.getString(GameZoneTag.FUNC);
        this.funcType = ZoneFuncType.fromName(funcTypeName);
        if (this.funcType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneFuncType: {}", funcTypeName);
        }

        String shapeTypeName = nbt.getString(GameZoneTag.SHAPE);
        this.shapeType = ZoneShapeType.fromName(shapeTypeName);
        if (this.shapeType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneShapeType: {}", shapeTypeName);
        } else if (this.shapeType == ZoneShapeType.POLYGON || this.shapeType == ZoneShapeType.STAR) {
            this.segments = nbt.getInt(GameZoneTag.SEGMENTS);
        }

        CompoundTag centerTag = nbt.getCompound(GameZoneTag.CENTER);
        this.center = new Vec3(centerTag.getDouble("x"), centerTag.getDouble("y"), centerTag.getDouble("z"));
        CompoundTag dimTag = nbt.getCompound(GameZoneTag.DIMENSION);
        this.dimension = new Vec3(dimTag.getDouble("x"), dimTag.getDouble("y"), dimTag.getDouble("z"));
        this.rotateDegree = nbt.contains(GameZoneTag.ROTATE) ? nbt.getDouble(GameZoneTag.ROTATE) : 0;
        this.progress = nbt.getDouble(GameZoneTag.PROGRESS);

        this.lastUpdateTime = ClientGameDataManager.currentTick; // 推迟到主线程
    }
}
