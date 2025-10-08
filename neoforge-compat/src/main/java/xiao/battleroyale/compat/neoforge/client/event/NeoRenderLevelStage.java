package xiao.battleroyale.compat.neoforge.client.event;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import xiao.battleroyale.api.client.event.RenderLevelStage;

import java.util.HashMap;
import java.util.Map;

/**
 * NeoForge和核心API渲染阶段的映射枚举。
 */
public enum NeoRenderLevelStage {
    AFTER_SKY(RenderLevelStageEvent.AfterSky.class, RenderLevelStage.AFTER_SKY),
    AFTER_OPAQUE_BLOCKS(RenderLevelStageEvent.AfterOpaqueBlocks.class, RenderLevelStage.AFTER_SOLID_BLOCKS),
//    AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS(RenderLevelStageEvent.AfterCutoutMippedBlocks.class, RenderLevelStage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS),
//    AFTER_CUTOUT_BLOCKS(RenderLevelStageEvent.AftetCutoutBlocks.class, RenderLevelStage.AFTER_CUTOUT_BLOCKS),
    AFTER_ENTITIES(RenderLevelStageEvent.AfterEntities.class, RenderLevelStage.AFTER_ENTITIES),
    AFTER_BLOCK_ENTITIES(RenderLevelStageEvent.AfterBlockEntities.class, RenderLevelStage.AFTER_BLOCK_ENTITIES),
    AFTER_TRANSLUCENT_BLOCKS(RenderLevelStageEvent.AfterTranslucentBlocks.class, RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS),
    AFTER_TRIPWIRE_BLOCKS(RenderLevelStageEvent.AfterTripwireBlocks.class, RenderLevelStage.AFTER_TRIPWIRE_BLOCKS),
    AFTER_PARTICLES(RenderLevelStageEvent.AfterParticles.class, RenderLevelStage.AFTER_PARTICLES),
    AFTER_WEATHER(RenderLevelStageEvent.AfterWeather.class, RenderLevelStage.AFTER_WEATHER),
    AFTER_LEVEL(RenderLevelStageEvent.AfterLevel.class, RenderLevelStage.AFTER_LEVEL);

    private final Class<? extends RenderLevelStageEvent> neoForgeEventClass;
    private final RenderLevelStage renderLevelStage;

    NeoRenderLevelStage(Class<? extends RenderLevelStageEvent> neoForgeEventClass, RenderLevelStage renderLevelStage) {
        this.neoForgeEventClass = neoForgeEventClass;
        this.renderLevelStage = renderLevelStage;
    }

    public Class<? extends RenderLevelStageEvent> getNeoForgeEventClass() {
        return neoForgeEventClass;
    }
    public RenderLevelStage getRenderLevelStage() {
        return renderLevelStage;
    }

    private static final Map<Class<? extends RenderLevelStageEvent>, RenderLevelStage> NEOFORGE_TO_CORE = new HashMap<>(); // 静态映射名可以更新

    static {
        for (NeoRenderLevelStage type : values()) {
            NEOFORGE_TO_CORE.put(type.neoForgeEventClass, type.renderLevelStage);
        }
    }

    public static RenderLevelStage fromEventClass(Class<? extends RenderLevelStageEvent> eventClass) {
        return NEOFORGE_TO_CORE.get(eventClass);
    }
}