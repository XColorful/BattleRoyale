package xiao.battleroyale.config.common.game.spawn.type.detail;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.spawn.type.detail.SpawnDetailTag;
import xiao.battleroyale.config.common.game.spawn.type.shape.SpawnShapeType;
import xiao.battleroyale.util.JsonUtils;

public class PlaneDetailEntry extends AbstractDetailEntry<PlaneDetailEntry> {

    public double planeHeight;
    public double planeSpeed;
    public boolean fixedReachTime;

    public PlaneDetailEntry(double planeHeight, double planeSpeed, boolean fixedReachTime) {
        this.planeHeight = planeHeight;
        this.planeSpeed = planeSpeed;
        this.fixedReachTime = fixedReachTime;
    }

    @Override
    public void toJson(JsonObject jsonObject, SpawnShapeType shapeType, CommonDetailType detailType) {
        jsonObject.addProperty(SpawnDetailTag.PLANE_HEIGHT, planeHeight);
        jsonObject.addProperty(SpawnDetailTag.PLANE_SPEED, planeSpeed);
        jsonObject.addProperty(SpawnDetailTag.PLANE_FIXED_TIME, fixedReachTime);
    }

    public static @NotNull PlaneDetailEntry fromJson(JsonObject jsonObject, CommonDetailType detailType) {
        double height = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.PLANE_HEIGHT, 0);
        double speed = JsonUtils.getJsonDouble(jsonObject, SpawnDetailTag.PLANE_SPEED, 1);
        boolean fixedTime = JsonUtils.getJsonBool(jsonObject, SpawnDetailTag.PLANE_FIXED_TIME, false);

        return new PlaneDetailEntry(height, speed, fixedTime);
    }
}
