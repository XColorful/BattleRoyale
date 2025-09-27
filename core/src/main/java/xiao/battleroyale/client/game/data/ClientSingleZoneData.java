package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.network.message.zone.GameZoneTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;

public class ClientSingleZoneData extends AbstractClientExpireData {

    private static boolean useClientColor = false;
    public static void setUseClientColor(boolean use) { useClientColor = use; }
    private static String clientColorString = "#0000FF";
    public static void setClientColorString(String colorString) { clientColorString = colorString; }

    public final int id;
    public String name;
    public Color color;
    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;
    public ZoneFuncType funcType;
    public ZoneShapeType shapeType;
    public Vec3 center;
    public Vec3 dimension;
    public double rotateDegree = 0;
    public int segments = 3; // 供多边形和星形使用
    public double progress; // [0, 1]

    public ClientSingleZoneData(int id) {
        this.id = id;
    }

    /*
     * 需推迟到主线程
     */
    @Override
    public void updateFromNbt(@NotNull CompoundTag nbt) {
        this.lastMessageNbt = nbt;

        this.name = nbt.getString(GameZoneTag.ZONE_NAME);
        this.color = ColorUtils.parseColorFromString(nbt.getString(GameZoneTag.ZONE_COLOR));
        if (useClientColor) {
            this.color = ColorUtils.changeColorExceptAlpha(this.color, clientColorString);
        }
        this.r = this.color.getRed() / 255.0F;
        this.g = this.color.getGreen() / 255.0F;
        this.b = this.color.getBlue() / 255.0F;
        this.a = this.color.getAlpha() / 255.0F;

        String funcTypeName = nbt.getString(GameZoneTag.FUNC);
        this.funcType = ZoneFuncType.fromName(funcTypeName);
        if (this.funcType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneFuncType: {}", funcTypeName);
        }

        String shapeTypeName = nbt.getString(GameZoneTag.SHAPE);
        this.shapeType = ZoneShapeType.fromName(shapeTypeName);
        if (this.shapeType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneShapeType: {}", shapeTypeName);
        } else {
            switch (this.shapeType) {
                case POLYGON, STAR:
                    this.segments = nbt.getInt(GameZoneTag.SEGMENTS);
                    break;
                // case CIRCLE, ELLIPSE, SPHERE, ELLIPSOID:
            }
        }

        CompoundTag centerTag = nbt.getCompound(GameZoneTag.CENTER);
        this.center = new Vec3(centerTag.getDouble("x"), centerTag.getDouble("y"), centerTag.getDouble("z"));
        CompoundTag dimTag = nbt.getCompound(GameZoneTag.DIMENSION);
        this.dimension = new Vec3(dimTag.getDouble("x"), dimTag.getDouble("y"), dimTag.getDouble("z"));
        this.rotateDegree = nbt.contains(GameZoneTag.ROTATE) ? nbt.getDouble(GameZoneTag.ROTATE) : 0;
        this.progress = nbt.getDouble(GameZoneTag.SHAPE_PROGRESS);

        this.lastUpdateTick = ClientGameDataManager.getCurrentTick(); // 推迟到主线程
    }
}
