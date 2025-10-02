package xiao.battleroyale.compat.neoforge.client.event;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import xiao.battleroyale.api.client.event.RenderLevelStage;

import java.util.HashMap;
import java.util.Map;

/**
 * NeoForge和核心API渲染阶段的映射枚举。
 */
public enum NeoRenderLevelStage {
    AFTER_SKY(Stage.AFTER_SKY, RenderLevelStage.AFTER_SKY),
    AFTER_SOLID_BLOCKS(Stage.AFTER_SOLID_BLOCKS, RenderLevelStage.AFTER_SOLID_BLOCKS),
    AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS(Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS, RenderLevelStage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS),
    AFTER_CUTOUT_BLOCKS(Stage.AFTER_CUTOUT_BLOCKS, RenderLevelStage.AFTER_CUTOUT_BLOCKS),
    AFTER_ENTITIES(Stage.AFTER_ENTITIES, RenderLevelStage.AFTER_ENTITIES),
    AFTER_BLOCK_ENTITIES(Stage.AFTER_BLOCK_ENTITIES, RenderLevelStage.AFTER_BLOCK_ENTITIES),
    AFTER_TRANSLUCENT_BLOCKS(Stage.AFTER_TRANSLUCENT_BLOCKS, RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS),
    AFTER_TRIPWIRE_BLOCKS(Stage.AFTER_TRIPWIRE_BLOCKS, RenderLevelStage.AFTER_TRIPWIRE_BLOCKS),
    AFTER_PARTICLES(Stage.AFTER_PARTICLES, RenderLevelStage.AFTER_PARTICLES),
    AFTER_WEATHER(Stage.AFTER_WEATHER, RenderLevelStage.AFTER_WEATHER),
    AFTER_LEVEL(Stage.AFTER_LEVEL, RenderLevelStage.AFTER_LEVEL);

    private final Stage stage;
    private final RenderLevelStage renderLevelStage;

    NeoRenderLevelStage(Stage stage, RenderLevelStage renderLevelStage) {
        this.stage = stage;
        this.renderLevelStage = renderLevelStage;
    }

    public Stage getStage() {
        return stage;
    }
    public RenderLevelStage getRenderLevelStage() {
        return renderLevelStage;
    }

    private static final Map<Stage, RenderLevelStage> NEOFORGE_TO_CORE = new HashMap<>(); // 静态映射名可以更新

    static {
        for (NeoRenderLevelStage type : values()) {
            NEOFORGE_TO_CORE.put(type.stage, type.renderLevelStage);
        }
    }

    public static RenderLevelStage fromStage(Stage stage) {
        return NEOFORGE_TO_CORE.get(stage);
    }
}