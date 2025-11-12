package xiao.battleroyale.config.common.game.zone.custom;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;

import java.util.HashSet;
import java.util.Set;

public class SpecialRenderProtocol {

    public final boolean hideInSpectate;
    public final double heightOffset;
    public final double side;
    public final boolean _3dDistance;
    public final String distDimStr;
    public final double distMul;
    public final double distAdd;
    public final @Nullable String innerColor;
    public final @Nullable String outsideColor;
    public final boolean hasCustomColor;


    @ApiStatus.Internal
    public SpecialRenderProtocol(boolean hideInSpectate, double heightOffset, double side, boolean _3dDistance, String distDimStr, double distMul, double distAdd, @Nullable String innerColor, @Nullable String outsideColor) {
        this.hideInSpectate = hideInSpectate;
        this.heightOffset = heightOffset;
        this.side = side;
        this._3dDistance = _3dDistance;
        this.distDimStr = distDimStr;
        this.distMul = distMul;
        this.distAdd = distAdd;
        this.innerColor = innerColor;
        this.outsideColor = outsideColor;
        this.hasCustomColor = this.innerColor != null || this.outsideColor != null;
    }

    public static @Nullable SpecialRenderProtocol getConfigFromProtocol(String protocol, @NotNull JsonObject jsonTag) {
        if (protocol == null || protocol.isEmpty()) {
            return null;
        }
        String[] parts = protocol.split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        String namespace = parts[0];
        String version = parts[1];
        if (namespace.equals(BattleRoyale.MOD_ID) || namespace.equals(BattleRoyale.MOD_NAME_SHORT)) {
            switch (version) {
                case "direct" -> {
                    return SpecialRenderProtocol046.fromTag(jsonTag);
                }
                default -> {
                    if (!unknownVersion.contains(version)) {
                        BattleRoyale.LOGGER.info("SpecialRenderProtocol: unknown version {}", version);
                        unknownVersion.add(version);
                    }
                    return SpecialRenderProtocol046.fromTag(jsonTag);
                }
            }
        }
        return null;
    }

    private static final Set<String> unknownVersion = new HashSet<>();
}
