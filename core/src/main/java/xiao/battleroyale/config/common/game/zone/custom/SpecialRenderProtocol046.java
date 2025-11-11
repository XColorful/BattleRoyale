package xiao.battleroyale.config.common.game.zone.custom;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.util.JsonUtils;

// battleroyale0.4.6
public class SpecialRenderProtocol046 {

    public static final String HIDE_IN_SPECTATE = "hideInSpectate";
    public static final String HEIGHT_OFFSET = "heightOffset";
    public static final String SIDE = "side";
    public static final String _3D_DISTANCE = "3dDistance";
    public static final String DISTANCE_MULTIPLY = "distMul";
    public static final String DISTANCE_ADD = "distAdd";
    public static final String SPLIT_DISTANCE = "splitDistance";
    public static final String INNER_COLOR = "innerColor";
    public static final String OUTSIDE_COLOR = "outsideColor";

    protected static SpecialRenderProtocol fromTag(@NotNull JsonObject jsonTag) {
        try {
            boolean hideInSpectate = JsonUtils.getJsonBool(jsonTag, HIDE_IN_SPECTATE, true);
            double heightOffset = JsonUtils.getJsonDouble(jsonTag, HEIGHT_OFFSET, 0.25);
            double side = JsonUtils.getJsonDouble(jsonTag, SIDE, 0.25);
            boolean _3dDistance = JsonUtils.getJsonBool(jsonTag, _3D_DISTANCE, false);
            double distMul = JsonUtils.getJsonDouble(jsonTag, DISTANCE_MULTIPLY, 1);
            double distAdd = JsonUtils.getJsonDouble(jsonTag, DISTANCE_ADD, 0);
            double splitDistance = JsonUtils.getJsonDouble(jsonTag, SPLIT_DISTANCE, -1);
            if (splitDistance < 0) {
                return new SpecialRenderProtocol(hideInSpectate, heightOffset, side, _3dDistance, distMul, distAdd);
            }
            String innerColor = JsonUtils.getJsonString(jsonTag, INNER_COLOR, null);
            String outsideColor = JsonUtils.getJsonString(jsonTag, OUTSIDE_COLOR, null);
            return new SpecialRenderProtocol(hideInSpectate, heightOffset, side, _3dDistance, distMul, distAdd, splitDistance, innerColor, outsideColor);
        } catch (Exception e) {
            return null;
        }
    }
}
