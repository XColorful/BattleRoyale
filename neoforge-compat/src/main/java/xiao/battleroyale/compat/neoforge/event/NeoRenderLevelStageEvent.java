package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IRenderLevelStageEvent;
import xiao.battleroyale.api.event.RenderLevelStage;

public class NeoRenderLevelStageEvent extends NeoEvent implements IRenderLevelStageEvent {

    private final RenderLevelStageEvent typedEvent;
    private final RenderLevelStage stage;

    public NeoRenderLevelStageEvent(RenderLevelStageEvent event) {
        super(event);
        this.typedEvent = event;
        this.stage = NeoRenderLevelStage.fromStage(event.getStage());
    }
    @Override public EventType getType() {
        return EventType.RENDER_LEVEL_STAGE_EVENT;
    }

    @Override
    public RenderLevelStage getStage() {
        return this.stage;
    }

    @Override
    public Matrix4f getModelViewMatrix() {
        return this.typedEvent.getPoseStack().last().pose();
    }

    @Override
    public Vec3 getCamera_getPosition() {
        return this.typedEvent.getCamera().getPosition();
    }

    @Override
    public float getPartialTick() {
        return this.typedEvent.getPartialTick().getGameTimeDeltaPartialTick(true);
    }

    @Override
    public @Nullable CommandSourceStack createCommandSourceStack(@Nullable CommandSource source) {
        return null;
    }

    @Override public String getTextName() {
        return "NeoRenderLevelStageEvent";
    }
    @Override public Component getDisplayName() {
        return Component.literal(getTextName());
    }
}