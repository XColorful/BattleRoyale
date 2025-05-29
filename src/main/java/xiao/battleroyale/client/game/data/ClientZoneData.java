package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.GameTag;
import xiao.battleroyale.config.common.game.zone.zonefunc.ZoneFuncType;
import xiao.battleroyale.config.common.game.zone.zoneshape.ZoneShapeType;

import java.awt.*;

public class ClientZoneData {

    public final int id;
    public String name;
    public Color color;

    public ZoneFuncType funcType;

    public ZoneShapeType shapeType;
    public Vec3 center;
    public Vec3 dimension;

    public double progress; // [0, 1]

    public ClientZoneData(int id) {
        this.id = id;
    }

    public void updateFromNbt(CompoundTag nbt) {
        this.name = nbt.getString(GameTag.ZONE_NAME);
        String colorHex = nbt.getString(GameTag.ZONE_COLOR);
        try {
            // 尝试解析带透明度的颜色字符串
            if (colorHex.length() == 9 && colorHex.startsWith("#")) { // #RRGGBBAA
                int rgba = (int) Long.parseLong(colorHex.substring(1), 16); // 解析整个8位hex
                int r = (rgba >> 24) & 0xFF;
                int g = (rgba >> 16) & 0xFF;
                int b = (rgba >> 8) & 0xFF;
                int a = rgba & 0xFF;
                this.color = new Color(r, g, b, a);

            } else { // 假设是 #RRGGBB
                this.color = Color.decode(colorHex); // 默认 Alpha 为 255
            }
        } catch (NumberFormatException e) {
            BattleRoyale.LOGGER.warn("Failed to decode zone color hex: {}, reason: {}", colorHex, e.getMessage());
            this.color = Color.WHITE;
        }

        String funcTypeName = nbt.getString(GameTag.FUNC);
        this.funcType = ZoneFuncType.fromName(funcTypeName);
        if (this.funcType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneFuncType: {}", funcTypeName);
        }

        String shapeTypeName = nbt.getString(GameTag.SHAPE);
        this.shapeType = ZoneShapeType.fromName(shapeTypeName);
        if (this.shapeType == null) {
            BattleRoyale.LOGGER.warn("Unknown ZoneShapeType: {}", shapeTypeName);
        }

        CompoundTag centerTag = nbt.getCompound(GameTag.CENTER);
        this.center = new Vec3(centerTag.getDouble("x"), centerTag.getDouble("y"), centerTag.getDouble("z"));

        CompoundTag dimTag = nbt.getCompound(GameTag.DIMENSION);
        this.dimension = new Vec3(dimTag.getDouble("x"), dimTag.getDouble("y"), dimTag.getDouble("z"));

        this.progress = nbt.getDouble(GameTag.PROGRESS);
    }
}
