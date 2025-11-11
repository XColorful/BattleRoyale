package xiao.battleroyale.api.event.client.render;

import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import xiao.battleroyale.api.client.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.client.render.game.level.IClientSimpleZoneRenderer;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;

public class SpecialZoneRenderEvent extends AbstractSpecialRenderEvent {

    protected final IRenderLevelStageEvent event;
    protected final IClientSimpleZoneRenderer clientZoneRenderer;
    protected final ClientSingleZoneData zoneData;

    public SpecialZoneRenderEvent(IClientGameDataManager clientGameDataManager, String protocol, @NotNull JsonObject jsonTag,
                                  IRenderLevelStageEvent event, IClientSimpleZoneRenderer clientZoneRenderer, ClientSingleZoneData zoneData) {
        super(clientGameDataManager, protocol, jsonTag);
        this.event = event;
        this.clientZoneRenderer = clientZoneRenderer;
        this.zoneData = zoneData;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.SPECIAL_ZONE_RENDER_EVENT;
    }

    public IRenderLevelStageEvent getRenderEvent() {
        return event;
    }

    public Matrix4f getBaseModelView() {
        return getRenderEvent().getModelViewMatrix();
    }

    public Vec3 getCameraPos() {
        return getRenderEvent().getCamera_getPosition();
    }

    public float getPartialTick() {
        return getRenderEvent().getPartialTick();
    }

    /**
     * 需手动转成IClientZoneRenderer
     */
    public IClientSimpleZoneRenderer getClientZoneRenderer() {
        return clientZoneRenderer;
    }

    public ClientSingleZoneData getZoneData() {
        return zoneData;
    }
}
