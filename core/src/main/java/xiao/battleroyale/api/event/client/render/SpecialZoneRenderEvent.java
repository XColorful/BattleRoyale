package xiao.battleroyale.api.event.client.render;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.client.render.level.IClientSimpleZoneRenderer;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.event.EventDispatcher;

public class SpecialZoneRenderEvent extends AbstractSpecialRenderEvent {

    protected final ISubmitCustomGeometryEvent event;
    protected final IClientSimpleZoneRenderer clientZoneRenderer;
    protected final ClientSingleZoneData zoneData;

    public SpecialZoneRenderEvent(IClientGameDataManager clientGameDataManager, String protocol, @NotNull JsonObject jsonTag,
                                  ISubmitCustomGeometryEvent event, IClientSimpleZoneRenderer clientZoneRenderer, ClientSingleZoneData zoneData) {
        super(clientGameDataManager, protocol, jsonTag);
        this.event = event;
        this.clientZoneRenderer = clientZoneRenderer;
        this.zoneData = zoneData;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.SPECIAL_ZONE_RENDER_EVENT;
    }

    public ISubmitCustomGeometryEvent getRenderEvent() {
        return event;
    }

    public Matrix4f getBaseModelView() {
        return getRenderEvent().getPoseStack().last().pose();
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

    @Override public String getTextName() {
        return "CBR client SpecialZoneRenderEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }

    private static final EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> _EVENT_DISPATCHER = BattleRoyale.getEventPoster().getEventDispatcher(SpecialZoneRenderEvent.class);
    @Override public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher() {
        return _EVENT_DISPATCHER;
    }
}
