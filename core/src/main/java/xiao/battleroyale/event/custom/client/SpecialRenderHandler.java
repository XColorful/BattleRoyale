package xiao.battleroyale.event.custom.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.client.render.SpecialZoneRenderEvent;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.renderer.level.Shape3D;
import xiao.battleroyale.client.renderer.level.ZoneRenderer;
import xiao.battleroyale.config.common.game.zone.custom.SpecialRenderProtocol;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;

/**
 * 仅客户端调用
 */
public class SpecialRenderHandler implements ICustomEventHandler {

    private static class SpecialRenderHandlerHolder {
        private static final SpecialRenderHandler INSTANCE = new SpecialRenderHandler();
    }

    public static SpecialRenderHandler get() {
        return SpecialRenderHandlerHolder.INSTANCE;
    }

    private SpecialRenderHandler() {}

    @Override
    public String getEventHandlerName() {
        return String.format("%s:SpecialRenderHandler", BattleRoyale.MOD_ID);
    }

    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        if (customEventType == CustomEventType.SPECIAL_ZONE_RENDER_EVENT) {
            // 检查协议是否指向该模组
            SpecialZoneRenderEvent renderEvent = (SpecialZoneRenderEvent) event;
            SpecialRenderProtocol renderProtocol = SpecialRenderProtocol.getConfigFromProtocol(renderEvent.getProtocol(), renderEvent.getJsonTag());
            if (renderProtocol == null) {
                return;
            }

            // 基本API
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) {
                BattleRoyale.LOGGER.warn("In SpecialRenderHandler, mc.serverLevel == null || mc.player == null");
                return;
            }
            // Fail-Fast
            @Nullable VertexConsumer consumer = renderEvent.getClientZoneRenderer().getVertexConsumer();
            if (consumer == null) {
                String exceptionMessage = String.format(
                        "%s: clientZoneRenderer.getVertexConsumer() returned null. " +
                                "A VertexConsumer is required for special zone rendering. " +
                                "If IClientZoneRenderer is replaced by another mod, " +
                                "that mod's implementation must ensure a valid VertexConsumer is set before/during this event.",
                        getEventHandlerName()
                );
                throw new IllegalStateException(exceptionMessage);
            }
            ClientSingleZoneData zoneData = renderEvent.getZoneData();
            Matrix4f baseModelView = renderEvent.getBaseModelView();
            Vec3 cameraPos = renderEvent.getCameraPos();

            // --------核心计算部分--------

            // 旁观模式隐藏
            if (renderProtocol.hideInSpectate && mc.player.isSpectator()) {
                return;
            }

            // 距离计算
            // 玩家位置 (A)
            float partialTicks = renderEvent.getPartialTick();
            Vec3 lastTickPos = new Vec3(mc.player.xOld, mc.player.yOld, mc.player.zOld);
            Vec3 currentTickPos = mc.player.position();
            Vec3 interpolatedPlayerPos = lastTickPos.lerp(currentTickPos, partialTicks);
            double px = interpolatedPlayerPos.x;
            double py = interpolatedPlayerPos.y;
            double pz = interpolatedPlayerPos.z;
            // 区域中心 (C) 的XZ投影
            double cx = zoneData.center.x;
            double cz = zoneData.center.z;
            // 投影向量分量
            double deltaX = cx - px;
            double deltaZ = cz - pz;
            // 线段AB的长度 n
            double n = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            if (n < 0.001) {
                return;
            }

            double distance;
            if (renderProtocol._3dDistance) {
                double cy = zoneData.center.y;
                double deltaY = cy - py;
                distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            } else { // 平面距离
                distance = n;
            }
            double splitDistance = switch (renderProtocol.distDimStr) {
                case "x" -> zoneData.dimension.x;
                case "y" -> zoneData.dimension.y;
                case "z" -> zoneData.dimension.z;
                default -> 0;
            };
            boolean isInRange = distance <= splitDistance * renderProtocol.distMul + renderProtocol.distAdd;
            float r, g, b, a;
            String colorString = !renderProtocol.hasCustomColor ? null
                    : isInRange ? renderProtocol.innerColor : renderProtocol.outsideColor;
            if (colorString == null) {
                r = zoneData.r;
                g = zoneData.g;
                b = zoneData.b;
                a = zoneData.a;
            } else {
                Color color = ColorUtils.parseColorFromString(colorString);
                r = color.getRed() / 255.0F;
                g = color.getGreen() / 255.0F;
                b = color.getBlue() / 255.0F;
                a = color.getAlpha() / 255.0F;
            }

            // 长方体中心 M 的世界坐标
            Vec3 cuboidCenterWorld = new Vec3(
                    px + deltaX / 2.0,
                    py + renderProtocol.heightOffset,
                    pz + deltaZ / 2.0
            );
            // 绕Y轴的旋转角度 (弧度) - 使Z轴对齐向量(deltaX, deltaZ)
            double rotationRad = Math.atan2(deltaX, deltaZ);

            // --------渲染--------

            // 创建基础模型视图矩阵 (平移到长方体中心 M)
            Matrix4f cuboidMatrix = ZoneRenderer.createCenterOffsetMatrix(
                    renderEvent.getRenderEvent(), cuboidCenterWorld, cameraPos
            );
            // 应用旋转
            // 绕Y轴旋转，使长方体局部Z轴（长边）对齐线段AB
            cuboidMatrix.rotate(Axis.YP.rotation((float) rotationRad));
            double halfSide = renderProtocol.side / 2;
            Shape3D.drawFilledCuboid(
                    cuboidMatrix,
                    consumer,
                    r, g, b, a,
                    (float) halfSide, // X轴 (宽)
                    (float) halfSide, // Y轴 (高)
                    (float) (n / 2.0) // Z轴 (长)
            );
        } else {
            onReceiveWrongEvent(customEventType);
        }
    }
}
